namespace CollectionPlatform.Api.Controllers;

using Application;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

[ApiController, Route("api/v1/auth")]
public sealed class AuthenticationController(IAuthenticationService authentication) : ControllerBase
{
    [AllowAnonymous, HttpPost("register")]
    public async Task<ActionResult<AuthenticationResponse>> Register(RegisterRequest request, CancellationToken cancellationToken)
        => Ok(await authentication.RegisterAsync(request, cancellationToken));

    [AllowAnonymous, HttpPost("login")]
    public async Task<ActionResult<AuthenticationResponse>> Login(LoginRequest request, CancellationToken cancellationToken)
        => (await authentication.LoginAsync(request, cancellationToken)) is { } response ? Ok(response) : Unauthorized(new { message = "Invalid email or password." });

    [AllowAnonymous, HttpPost("refresh")]
    public async Task<ActionResult<AuthenticationResponse>> Refresh(RefreshRequest request, CancellationToken cancellationToken)
        => (await authentication.RefreshAsync(request.RefreshToken, cancellationToken)) is { } response ? Ok(response) : Unauthorized(new { message = "The refresh token is invalid or expired." });

    [AllowAnonymous, HttpPost("forgot-password")]
    public async Task<IActionResult> ForgotPassword(ForgotPasswordRequest request, CancellationToken cancellationToken)
    {
        await authentication.ForgotPasswordAsync(request, cancellationToken);
        return Ok(new { message = "If an account exists, password reset instructions have been sent." });
    }

    [AllowAnonymous, HttpPost("reset-password")]
    public async Task<IActionResult> ResetPassword(ResetPasswordRequest request, CancellationToken cancellationToken)
    => await authentication.ResetPasswordAsync(request, cancellationToken) ? Ok(new { message = "Password reset successfully." }) : BadRequest(new { message = "The reset link is invalid or expired." });

    [Authorize(AuthenticationSchemes = "Bearer"), HttpPost("logout")]
    public async Task<IActionResult> Logout(RefreshRequest request, CancellationToken cancellationToken)
    {
        await authentication.RevokeAsync(request.RefreshToken, cancellationToken);
        return NoContent();
    }
}