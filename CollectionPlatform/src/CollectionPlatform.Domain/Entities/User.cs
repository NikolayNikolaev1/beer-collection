namespace CollectionPlatform.Domain.Entities;

using Common.Extensions;

public sealed class User : AuditEntity
{
    private User() { }

    public User(Guid tenantId, string externalId, string? displayName) : base()
    {
        TenantId = tenantId;
        ExternalId = externalId.Require();
        DisplayName = displayName.NormalizeValue();
    }

    public Guid TenantId { get; private set; }
    public Tenant Tenant { get; private set; } = null!;
    public string ExternalId { get; private set; } = null!;
    public string? DisplayName { get; private set; }
    public Collection? Collection { get; private set; }

    public void Synchronize(string? displayName, bool isActive = true)
    {
        DisplayName = displayName.NormalizeValue();
        IsActive = isActive;
        UpdatedAt = DateTimeOffset.UtcNow;
    }

    public override void Deactivate() => Synchronize(DisplayName, false);
}
