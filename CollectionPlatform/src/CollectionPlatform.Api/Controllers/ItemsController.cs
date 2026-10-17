using CollectionPlatform.Application;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace CollectionPlatform.Api.Controllers;

[ApiController]
[Authorize]
[Route("api/v1/items")]
public sealed class ItemsController(IItemSynchronizationService items) : ControllerBase
{
    [HttpPost]
    public async Task<ActionResult<ItemSynchronizationResponse>> Synchronize(SynchronizeItemRequest request, CancellationToken cancellationToken)
    {
        var result = await items.SynchronizeAsync(request, cancellationToken);
        return Ok(new ItemSynchronizationResponse(result.Item, result.Status.ToString()));
    }

    [HttpPost("bulk")]
    public async Task<ActionResult<BulkSynchronizationResponse<BulkItemResult>>> SynchronizeBulk(
        BulkItemRequest request,
        CancellationToken cancellationToken)
    {
        if (request.Items.Count is < 1 or > BulkLimits.MaximumBatchSize)
        {
            return Problem(statusCode: StatusCodes.Status400BadRequest, title: "Invalid batch size", detail: $"A batch must contain between 1 and {BulkLimits.MaximumBatchSize} items.");
        }

        return Ok(await items.SynchronizeBulkAsync(request.Items, cancellationToken));
    }

    [HttpGet]
    public async Task<ActionResult<IReadOnlyList<ItemResponse>>> Get([FromQuery] int page = 1, [FromQuery] int pageSize = 50, [FromQuery] bool? active = null, CancellationToken cancellationToken = default)
    {
        if (page < 1 || pageSize is < 1 or > 200)
            return Problem(statusCode: StatusCodes.Status400BadRequest, title: "Invalid pagination", detail: "Page must be positive and pageSize must be between 1 and 200.");

        return Ok(await items.GetAsync(page, pageSize, active, cancellationToken));
    }
}
