using System.ComponentModel.DataAnnotations;

namespace CollectionPlatform.Application;

public static class BulkLimits
{
    public const int MaximumBatchSize = 500;
}

public sealed record BulkUserRequest(
    [property: Required, MaxLength(BulkLimits.MaximumBatchSize)] IReadOnlyList<SynchronizeUserRequest> Users);

public sealed record BulkItemRequest(
    [property: Required, MaxLength(BulkLimits.MaximumBatchSize)] IReadOnlyList<SynchronizeItemRequest> Items);

public sealed record BulkRecordError(string ExternalId, string Code, string Message);
public sealed record BulkUserResult(UserResponse User, string Status);
public sealed record BulkItemResult(ItemResponse Item, string Status);
public sealed record BulkSynchronizationResponse<T>(int Total, int Created, int Updated, int Unchanged, int Failed, IReadOnlyList<T> Results, IReadOnlyList<BulkRecordError> Errors);
