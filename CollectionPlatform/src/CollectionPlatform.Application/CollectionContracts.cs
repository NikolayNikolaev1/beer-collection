using System.ComponentModel.DataAnnotations;
using CollectionPlatform.Domain.Enums;

namespace CollectionPlatform.Application;

public sealed record AcquireCollectionItemRequest(
    [property: Required, StringLength(200, MinimumLength = 1)] string UserExternalId,
    [property: Required, StringLength(200, MinimumLength = 1)] string ItemExternalId,
    [property: Required, StringLength(200, MinimumLength = 1)] string ExternalEventId,
    [property: Range(1, int.MaxValue)] int Quantity = 1,
    [property: StringLength(200)] string? Source = null);

public sealed record CollectionItemResponse(Guid Id, string ExternalId, string Name, int Quantity, bool IsActive, IReadOnlyList<ItemAttributeResponse> Attributes);
public sealed record CollectionResponse(string UserExternalId, IReadOnlyList<CollectionItemResponse> Items, int Page, int PageSize);
public sealed record CollectionAcquisitionResponse(string ExternalEventId, string Status, CollectionItemResponse Item);

public interface ICollectionService
{
    Task<CollectionAcquisitionResponse> AcquireAsync(AcquireCollectionItemRequest request, CancellationToken cancellationToken);
    Task<CollectionResponse?> GetAsync(string userExternalId, int page, int pageSize, CancellationToken cancellationToken);
}
