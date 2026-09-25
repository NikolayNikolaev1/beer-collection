namespace CollectionPlatform.Domain.Entities;

public sealed class CollectionItem : AuditEntity
{
    private CollectionItem() { }

    public CollectionItem(Guid tenantId, Guid collectionId, Guid itemId, int quantity) : base()
    {
        if (quantity <= 0) throw new ArgumentOutOfRangeException(nameof(quantity));

        TenantId = tenantId;
        CollectionId = collectionId;
        ItemId = itemId;
        Quantity = quantity;
    }

    public Guid TenantId { get; private set; }

    public Guid CollectionId { get; private set; }

    public Guid ItemId { get; private set; }

    public Collection Collection { get; private set; } = null!;

    public Item Item { get; private set; } = null!;

    public int Quantity { get; private set; }

    public void AddQuantity(int quantity)
    {
        if (quantity <= 0) throw new ArgumentOutOfRangeException(nameof(quantity));

        checked { Quantity += quantity; }
        UpdatedAt = DateTimeOffset.UtcNow;
    }
}
