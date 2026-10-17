namespace CollectionPlatform.Domain.Entities;

public sealed class Collection : AuditEntity
{
    private Collection() { }

    public Collection(Guid tenantId, Guid userId) : base()
    {
        TenantId = tenantId;
        UserId = userId;
    }

    public Guid TenantId { get; private set; }

    public Guid UserId { get; private set; }

    public User User { get; private set; } = null!;

    public List<CollectionItem> Items { get; private set; } = [];
}
