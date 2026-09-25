using CollectionPlatform.Domain.Enums;

namespace CollectionPlatform.Application;

public interface ITenantContext
{
    Guid? TenantId { get; }
    Guid? ApiKeyId { get; }
    bool IsAuthenticated { get; }
}

public interface IClock
{
    DateTimeOffset UtcNow { get; }
}

public sealed class SystemClock : IClock
{
    public DateTimeOffset UtcNow => DateTimeOffset.UtcNow;
}

public sealed record SynchronizationResult(Guid Id, string ExternalId, SynchronizationStatus Status);
