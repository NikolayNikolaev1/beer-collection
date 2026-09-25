using System.Security.Claims;
using System.Text.Encodings.Web;
using CollectionPlatform.Application;
using CollectionPlatform.Infrastructure;
using Microsoft.AspNetCore.Authentication;
using Microsoft.Extensions.Options;

namespace CollectionPlatform.Api;

public sealed class ApiKeyAuthenticationHandler(
    IOptionsMonitor<AuthenticationSchemeOptions> options,
    ILoggerFactory logger,
    UrlEncoder encoder,
    IApiKeyValidator validator,
    TenantContext tenantContext) : AuthenticationHandler<AuthenticationSchemeOptions>(options, logger, encoder)
{
    public const string SchemeName = "ApiKey";

    protected override async Task<AuthenticateResult> HandleAuthenticateAsync()
    {
        if (!Request.Headers.TryGetValue("Authorization", out var headerValues))
        {
            return AuthenticateResult.NoResult();
        }

        var header = headerValues.ToString();
        const string prefix = SchemeName + " ";
        if (!header.StartsWith(prefix, StringComparison.OrdinalIgnoreCase))
        {
            return AuthenticateResult.Fail("Invalid authorization scheme.");
        }

        var credential = header[prefix.Length..].Trim();
        var result = await validator.ValidateAsync(credential, Context.RequestAborted);
        if (result is null)
        {
            Logger.LogWarning("API key authentication failed for request {TraceId}.", Context.TraceIdentifier);
            return AuthenticateResult.Fail("Invalid or revoked API key.");
        }

        tenantContext.Set(result.TenantId, result.ApiKeyId);
        var claims = new[]
        {
            new Claim(ClaimTypes.NameIdentifier, result.ApiKeyId.ToString()),
            new Claim("tenant_id", result.TenantId.ToString())
        };
        var identity = new ClaimsIdentity(claims, SchemeName);
        return AuthenticateResult.Success(new AuthenticationTicket(new ClaimsPrincipal(identity), SchemeName));
    }
}
