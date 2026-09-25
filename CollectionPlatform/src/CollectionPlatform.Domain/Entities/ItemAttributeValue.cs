namespace CollectionPlatform.Domain.Entities;

public sealed class ItemAttributeValue : AuditEntity
{
    private ItemAttributeValue() { }

    public ItemAttributeValue(Guid tenantId, Guid itemId, Guid attributeDefinitionId) : base()
    {
        TenantId = tenantId;
        ItemId = itemId;
        AttributeDefinitionId = attributeDefinitionId;
    }

    public Guid TenantId { get; private set; }

    public Guid ItemId { get; private set; }

    public Item Item { get; private set; } = null!;

    public Guid AttributeDefinitionId { get; private set; }

    public AttributeDefinition AttributeDefinition { get; private set; } = null!;

    public Guid? AttributeValueId { get; set; }

    public AttributeValue? AttributeValue { get; private set; }

    public string? TextValue { get; set; }

    public long? IntegerValue { get; set; }

    public decimal? DecimalValue { get; set; }

    public bool? BooleanValue { get; set; }

    public DateOnly? DateValue { get; set; }
}
