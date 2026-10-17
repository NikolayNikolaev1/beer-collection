using CollectionPlatform.Application;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace CollectionPlatform.Api.Controllers;

[ApiController]
[Authorize]
[Route("api/v1/users/{userExternalId}/collection")]
public sealed class UserCollectionsController(ICollectionService collections) : ControllerBase
{
    [HttpGet]
    public async Task<ActionResult<CollectionResponse>> Get(
        string userExternalId,
        [FromQuery] int page = 1,
        [FromQuery] int pageSize = 50,
        CancellationToken cancellationToken = default)
    {
        if (page < 1 || pageSize is < 1 or > 200)
            return Problem(statusCode: StatusCodes.Status400BadRequest, title: "Invalid pagination", detail: "Page must be positive and pageSize must be between 1 and 200.");

        var result = await collections.GetAsync(userExternalId, page, pageSize, cancellationToken);
        return result is null ? NotFound() : Ok(result);
    }
}