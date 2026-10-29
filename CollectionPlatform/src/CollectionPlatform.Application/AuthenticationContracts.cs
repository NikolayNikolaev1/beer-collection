namespace CollectionPlatform.Application;

using System.ComponentModel.DataAnnotations;

public sealed record RegisterRequest(
    [param: Required, StringLength(200, MinimumLength = 1)] string TenantName,
    [param: Required, EmailAddress, StringLength(256)] string Email,
    [param: Required, StringLength(100, MinimumLength = 3)] string Password);

public sealed record LoginRequest(
    [param: Required, EmailAddress] string Email,
    [param: Required] string Password);

public sealed record RefreshRequest([property: Required] string RefreshToken);

public sealed record ForgotPasswordRequest([property: Required, EmailAddress] string Email);

public sealed record ResetPasswordRequest(
    [param: Required] string UserId,
    [param: Required] string Token,
    [param: Required, StringLength(100, MinimumLength = 8)] string Password);

public sealed record UpdateProfileRequest(
    [param: Required, StringLength(200, MinimumLength = 1)] string TenantName,
    [param: Required, EmailAddress, StringLength(256)] string Email);

public sealed record ChangePasswordRequest(
    [param: Required] string CurrentPassword,
    [param: Required, StringLength(100, MinimumLength = 8)] string NewPassword);

public sealed record AuthenticationResponse(
    string AccessToken,
    string RefreshToken,
    DateTimeOffset ExpiresAt,
    ProfileResponse Profile);

public sealed record ProfileResponse(Guid Id, string TenantName, string Email);

public interface IAuthenticationService
{
    Task<AuthenticationResponse> RegisterAsync(RegisterRequest request, CancellationToken cancellationToken);
    Task<AuthenticationResponse?> LoginAsync(LoginRequest request, CancellationToken cancellationToken);
    Task<AuthenticationResponse?> RefreshAsync(string refreshToken, CancellationToken cancellationToken);
    Task RevokeAsync(string refreshToken, CancellationToken cancellationToken);
    Task ForgotPasswordAsync(ForgotPasswordRequest request, CancellationToken cancellationToken);
    Task<bool> ResetPasswordAsync(ResetPasswordRequest request, CancellationToken cancellationToken);
    Task<ProfileResponse> GetProfileAsync(CancellationToken cancellationToken);
    Task<ProfileResponse> UpdateProfileAsync(UpdateProfileRequest request, CancellationToken cancellationToken);
    Task<bool> ChangePasswordAsync(ChangePasswordRequest request, CancellationToken cancellationToken);
}