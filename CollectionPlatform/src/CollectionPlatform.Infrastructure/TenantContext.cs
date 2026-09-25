using CollectionPlatform.Application;

namespace CollectionPlatform.Infrastructure;

public sealed class TenantContext : ITenantContext
{
    public Guid? TenantId { get; private set; }
    public Guid? ApiKeyId { get; private set; }
    public bool IsAuthenticated => TenantId.HasValue && ApiKeyId.HasValue;

    public void Set(Guid tenantId, Guid apiKeyId)
    {
        TenantId = tenantId;
        ApiKeyId = apiKeyId;
    }
}
