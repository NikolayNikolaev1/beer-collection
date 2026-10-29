using CollectionPlatform.Application;

namespace CollectionPlatform.Infrastructure;

public sealed class TenantContext : ITenantContext
{
    public Guid? TenantId { get; private set; }
    public Guid? ApiKeyId { get; private set; }
    public bool IsAuthenticated => TenantId.HasValue;

    public void Set(Guid tenantId, Guid apiKeyId)
    {
        TenantId = tenantId;
        ApiKeyId = apiKeyId;
    }

    public void SetUser(Guid tenantId)
    {
        TenantId = tenantId;
        ApiKeyId = null;
    }
}
