using CollectionPlatform.Application;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace CollectionPlatform.Api.Controllers;

[ApiController]
[Authorize]
[Route("api/v1/collections")]
public sealed class CollectionsController(ICollectionService collections) : ControllerBase
{
    [HttpPost("acquisitions")]
    public async Task<ActionResult<CollectionAcquisitionResponse>> Acquire(AcquireCollectionItemRequest request, CancellationToken cancellationToken)
        => Ok(await collections.AcquireAsync(request, cancellationToken));

}
