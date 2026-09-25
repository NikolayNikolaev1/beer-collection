namespace CollectionPlatform.Domain.Entities;

using Common.Extensions;

public sealed class Item : AuditEntity
{
    private Item() { }

    public Item(Guid tenantId, string externalId, string name, string? description) : base()
    {
        TenantId = tenantId;
        ExternalId = externalId.Require();
        Name = name.Require();
        Description = description.NormalizeValue();
    }

    public Guid TenantId { get; private set; }

    public Tenant Tenant { get; private set; } = null!;

    public string ExternalId { get; private set; } = null!;

    public string Name { get; private set; } = null!;

    public string? Description { get; private set; }

    public IEnumerable<ItemAttributeValue> AttributeValues { get; private set; } = [];

    public IEnumerable<CollectionItem> CollectionItems { get; private set; } = [];

    public void Synchronize(string name, string? description, bool isActive = true)
    {
        Name = name.Require();
        Description = description.NormalizeValue();
        IsActive = isActive;
        UpdatedAt = DateTimeOffset.UtcNow;
    }
}
