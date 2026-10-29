namespace CollectionPlatform.Api.Controllers;

using Application;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

[ApiController, Authorize(AuthenticationSchemes = "Bearer"), Route("api/v1/profile")]
public sealed class ProfileController(IAuthenticationService authentication) : ControllerBase
{
    [HttpGet]
    public Task<ProfileResponse> Get(CancellationToken cancellationToken)
        => authentication.GetProfileAsync(cancellationToken);

    [HttpPut]
    public Task<ProfileResponse> Update(UpdateProfileRequest request, CancellationToken cancellationToken)
        => authentication.UpdateProfileAsync(request, cancellationToken);

    [HttpPost("password")]
    public async Task<IActionResult> ChangePassword(ChangePasswordRequest request, CancellationToken cancellationToken)
        => await authentication.ChangePasswordAsync(request, cancellationToken) ? Ok(new { message = "Password changed successfully." }) : BadRequest(new { message = "The current password is incorrect or the new password is invalid." });
}