namespace CollectionPlatform.Api.Controllers;

using Application;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

[ApiController]
[Authorize]
[Route("api/v1/users")]
public sealed class UsersController(IUserSynchronizationService users) : ControllerBase
{
    [HttpPost]
    [ProducesResponseType(typeof(UserSynchronizationResponse), StatusCodes.Status200OK)]
    public async Task<ActionResult<UserSynchronizationResponse>> Synchronize(
        SynchronizeUserRequest request,
        CancellationToken cancellationToken)
    {
        var result = await users.SynchronizeAsync(request, cancellationToken);
        return Ok(new UserSynchronizationResponse(result.User, result.Status.ToString()));
    }

    [HttpPost("bulk")]
    public async Task<ActionResult<BulkSynchronizationResponse<BulkUserResult>>> SynchronizeBulk(
        BulkUserRequest request,
        CancellationToken cancellationToken)
    {
        if (request.Users.Count is < 1 or > BulkLimits.MaximumBatchSize)
        {
            return Problem(statusCode: StatusCodes.Status400BadRequest, title: "Invalid batch size", detail: $"A batch must contain between 1 and {BulkLimits.MaximumBatchSize} users.");
        }

        return Ok(await users.SynchronizeBulkAsync(request.Users, cancellationToken));
    }

    [HttpGet]
    [ProducesResponseType(typeof(PagedResponse<UserResponse>), StatusCodes.Status200OK)]
    public async Task<ActionResult<PagedResponse<UserResponse>>> Get(
        [FromQuery] int page = 1,
        [FromQuery] int pageSize = 50,
        CancellationToken cancellationToken = default)
    {
        if (page < 1 || pageSize is < 1 or > 200)
        {
            return Problem(statusCode: StatusCodes.Status400BadRequest, title: "Invalid pagination", detail: "Page must be positive and pageSize must be between 1 and 200.");
        }

        var items = await users.GetAsync(page, pageSize, cancellationToken);
        return Ok(new PagedResponse<UserResponse>(items, page, pageSize, null));
    }
}

public sealed record UserSynchronizationResponse(UserResponse User, string Status);
public sealed record PagedResponse<T>(IReadOnlyList<T> Items, int Page, int PageSize, int? TotalCount);
