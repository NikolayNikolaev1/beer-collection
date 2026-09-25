namespace CollectionPlatform.Domain.Entities;

using Common.Extensions;

public sealed class ApiKey : AuditEntity
{
    private ApiKey() { }

    public ApiKey(Guid tenantId, string name, string keyPrefix, string keyHash) : base()
    {
        TenantId = tenantId;
        Name = name.Require();
        KeyPrefix = keyPrefix;
        KeyHash = keyHash;
    }

    public Guid TenantId { get; private set; }

    public Tenant Tenant { get; private set; } = null!;

    public string Name { get; private set; } = null!;

    public string KeyPrefix { get; private set; } = null!;

    public string KeyHash { get; private set; } = null!;

    public DateTimeOffset? RevokedAt { get; private set; }

    public DateTimeOffset? LastUsedAt { get; private set; }

    public void MarkUsed() => LastUsedAt = DateTimeOffset.UtcNow;

    public void Revoke()
    {
        Deactivate();
        RevokedAt = DateTimeOffset.UtcNow;
    }
}
