using System.ComponentModel.DataAnnotations;
using System.Text.Json;
using CollectionPlatform.Domain.Enums;

namespace CollectionPlatform.Application;

public sealed record SynchronizeItemRequest(
    [property: Required, StringLength(200, MinimumLength = 1)] string ExternalId,
    [property: Required, StringLength(300, MinimumLength = 1)] string Name,
    [property: StringLength(4000)] string? Description,
    Dictionary<string, JsonElement>? Attributes = null,
    bool IsActive = true);

public sealed record ItemAttributeResponse(string Key, AttributeDataType DataType, object? Value);
public sealed record ItemResponse(Guid Id, string ExternalId, string Name, string? Description, bool IsActive, DateTimeOffset UpdatedAt, IReadOnlyList<ItemAttributeResponse> Attributes);
public sealed record ItemSynchronizationResponse(ItemResponse Item, string Status);

public interface IItemSynchronizationService
{
    Task<(ItemResponse Item, SynchronizationStatus Status)> SynchronizeAsync(SynchronizeItemRequest request, CancellationToken cancellationToken);
    Task<BulkSynchronizationResponse<BulkItemResult>> SynchronizeBulkAsync(IReadOnlyList<SynchronizeItemRequest> requests, CancellationToken cancellationToken);
    Task<IReadOnlyList<ItemResponse>> GetAsync(int page, int pageSize, bool? active, CancellationToken cancellationToken);
}
