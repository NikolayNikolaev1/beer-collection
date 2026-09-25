namespace CollectionPlatform.Domain.Entities;

using Common.Extensions;

public sealed class CollectionAcquisition : AuditEntity
{
    private CollectionAcquisition() { }

    public CollectionAcquisition(
        Guid tenantId,
        string externalEventId,
        Guid userId,
        Guid itemId,
        int quantity,
        string? source) : base()
    {
        if (quantity <= 0) throw new ArgumentOutOfRangeException(nameof(quantity));

        TenantId = tenantId;
        ExternalEventId = externalEventId.Require();
        UserId = userId;
        ItemId = itemId;
        Quantity = quantity;
        Source = source.NormalizeValue();
    }

    public Guid TenantId { get; private set; }

    public string ExternalEventId { get; private set; } = null!;

    public Guid UserId { get; private set; }

    public Guid ItemId { get; private set; }

    public int Quantity { get; private set; }

    public string? Source { get; private set; }
}
