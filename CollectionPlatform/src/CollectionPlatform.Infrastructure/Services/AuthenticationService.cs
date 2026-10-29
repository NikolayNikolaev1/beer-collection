namespace CollectionPlatform.Infrastructure.Services;

using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Text;
using Application;
using Domain.Entities;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Microsoft.IdentityModel.Tokens;

public interface IPasswordResetEmailSender
{
    Task SendAsync(string email, string userId, string token, CancellationToken cancellationToken);
}

public sealed class DevelopmentPasswordResetEmailSender(ILogger<DevelopmentPasswordResetEmailSender> logger) : IPasswordResetEmailSender
{
    public Task SendAsync(string email, string userId, string token, CancellationToken cancellationToken)
    {
        logger.LogInformation("Development password reset link for {Email}: /reset-password?userId={UserId}&token={Token}", email, userId, token);
        return Task.CompletedTask;
    }
}

public sealed class AuthenticationService(
    UserManager<Tenant> userManager,
    CollectionPlatformDbContext dbContext,
    TenantContext tenantContext,
    IConfiguration configuration,
    IPasswordResetEmailSender emailSender) : IAuthenticationService
{
    private const int AccessTokenMinutes = 15;

    public async Task<AuthenticationResponse> RegisterAsync(RegisterRequest request, CancellationToken cancellationToken)
    {
        var tenant = new Tenant(request.TenantName)
        {
            Email = request.Email.Trim(),
            UserName = request.Email.Trim(),
            EmailConfirmed = true
        };
        var result = await userManager.CreateAsync(tenant, request.Password);
        if (!result.Succeeded) throw new ArgumentException(string.Join(" ", result.Errors.Select(error => error.Description)));
        tenantContext.SetUser(tenant.Id);
        return await CreateResponseAsync(tenant, cancellationToken);
    }

    public async Task<AuthenticationResponse?> LoginAsync(LoginRequest request, CancellationToken cancellationToken)
    {
        var tenant = await userManager.FindByEmailAsync(request.Email.Trim());
        if (tenant is null || !tenant.IsActive) return null;
        if (!await userManager.CheckPasswordAsync(tenant, request.Password)) return null;
        tenantContext.SetUser(tenant.Id);
        return await CreateResponseAsync(tenant, cancellationToken);
    }

    public async Task<AuthenticationResponse?> RefreshAsync(string refreshToken, CancellationToken cancellationToken)
    {
        var current = await dbContext.RefreshTokens.IgnoreQueryFilters().SingleOrDefaultAsync(token => token.TokenHash == Hash(refreshToken), cancellationToken);
        if (current is null || !current.IsActive)
        {
            if (current?.RevokedAt is not null) await RevokeTenantTokensAsync(current.TenantId, cancellationToken);
            return null;
        }
        var tenant = await userManager.FindByIdAsync(current.TenantId.ToString());
        if (tenant is null || !tenant.IsActive) return null;
        tenantContext.SetUser(tenant.Id);
        var response = await CreateResponseAsync(tenant, cancellationToken);
        var replacement = await dbContext.RefreshTokens.IgnoreQueryFilters().SingleAsync(token => token.TokenHash == Hash(response.RefreshToken), cancellationToken);
        current.Revoke(replacement.Id);
        await dbContext.SaveChangesAsync(cancellationToken);
        return response;
    }

    public async Task RevokeAsync(string refreshToken, CancellationToken cancellationToken)
    {
        var token = await dbContext.RefreshTokens.IgnoreQueryFilters().SingleOrDefaultAsync(candidate => candidate.TokenHash == Hash(refreshToken), cancellationToken);
        if (token is null) return;
        token.Revoke();
        await dbContext.SaveChangesAsync(cancellationToken);
    }

    public async Task ForgotPasswordAsync(ForgotPasswordRequest request, CancellationToken cancellationToken)
    {
        var tenant = await userManager.FindByEmailAsync(request.Email.Trim());
        if (tenant is null || string.IsNullOrWhiteSpace(tenant.Email)) return;
        var token = await userManager.GeneratePasswordResetTokenAsync(tenant);
        await emailSender.SendAsync(tenant.Email, tenant.Id.ToString(), token, cancellationToken);
    }

    public async Task<bool> ResetPasswordAsync(ResetPasswordRequest request, CancellationToken cancellationToken)
    {
        var tenant = await userManager.FindByIdAsync(request.UserId);
        if (tenant is null) return false;
        var result = await userManager.ResetPasswordAsync(tenant, request.Token, request.Password);
        return result.Succeeded;
    }

    public async Task<ProfileResponse> GetProfileAsync(CancellationToken cancellationToken) => ToProfile(await RequireCurrentTenantAsync());

    public async Task<ProfileResponse> UpdateProfileAsync(UpdateProfileRequest request, CancellationToken cancellationToken)
    {
        var tenant = await RequireCurrentTenantAsync();
        tenant.UpdateProfile(request.TenantName, request.Email);
        var result = await userManager.UpdateAsync(tenant);
        if (!result.Succeeded) throw new ArgumentException(string.Join(" ", result.Errors.Select(error => error.Description)));
        return ToProfile(tenant);
    }

    public async Task<bool> ChangePasswordAsync(ChangePasswordRequest request, CancellationToken cancellationToken)
    {
        var result = await userManager.ChangePasswordAsync(await RequireCurrentTenantAsync(), request.CurrentPassword, request.NewPassword);
        return result.Succeeded;
    }

    private async Task<AuthenticationResponse> CreateResponseAsync(Tenant tenant, CancellationToken cancellationToken)
    {
        var expiresAt = DateTimeOffset.UtcNow.AddMinutes(AccessTokenMinutes);
        var key = configuration["Authentication:SigningKey"] ?? throw new InvalidOperationException("Authentication signing key is not configured.");
        if (key.Length < 32) throw new InvalidOperationException("Authentication signing key must be at least 32 characters.");
        var claims = new[]
        {
            new Claim(JwtRegisteredClaimNames.Sub, tenant.Id.ToString()),
            new Claim(JwtRegisteredClaimNames.Email, tenant.Email ?? string.Empty),
            new Claim("tenant_id", tenant.Id.ToString()),
            new Claim(ClaimTypes.Name, tenant.UserName ?? tenant.Email ?? tenant.Id.ToString())
        };
        var credentials = new SigningCredentials(new SymmetricSecurityKey(Encoding.UTF8.GetBytes(key)), SecurityAlgorithms.HmacSha256);
        var jwt = new JwtSecurityToken(configuration["Authentication:Issuer"], configuration["Authentication:Audience"], claims, expires: expiresAt.UtcDateTime, signingCredentials: credentials);
        var refreshValue = Convert.ToBase64String(RandomNumberGenerator.GetBytes(64));
        await dbContext.RefreshTokens.AddAsync(new RefreshToken(tenant.Id, Hash(refreshValue), DateTimeOffset.UtcNow.AddDays(30)), cancellationToken);
        await dbContext.SaveChangesAsync(cancellationToken);
        return new AuthenticationResponse(new JwtSecurityTokenHandler().WriteToken(jwt), refreshValue, expiresAt, ToProfile(tenant));
    }

    private async Task<Tenant> RequireCurrentTenantAsync()
    {
        var id = tenantContext.TenantId ?? throw new UnauthorizedAccessException("An authenticated account is required.");
        return await userManager.FindByIdAsync(id.ToString()) ?? throw new UnauthorizedAccessException("The account is no longer available.");
    }

    private async Task RevokeTenantTokensAsync(Guid tenantId, CancellationToken cancellationToken)
    {
        var tokens = await dbContext.RefreshTokens.IgnoreQueryFilters().Where(token => token.TenantId == tenantId && token.RevokedAt == null).ToListAsync(cancellationToken);
        foreach (var token in tokens) token.Revoke();
        await dbContext.SaveChangesAsync(cancellationToken);
    }

    private static ProfileResponse ToProfile(Tenant tenant) => new(tenant.Id, tenant.Name, tenant.Email ?? string.Empty);
    private static string Hash(string value) => Convert.ToHexString(SHA256.HashData(Encoding.UTF8.GetBytes(value)));
}