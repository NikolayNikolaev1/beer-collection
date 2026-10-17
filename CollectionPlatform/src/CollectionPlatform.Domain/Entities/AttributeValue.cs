namespace CollectionPlatform.Domain.Entities;

using Common.Extensions;

public sealed class AttributeValue : AuditEntity
{
    private AttributeValue() { }

    public AttributeValue(Guid tenantId, Guid attributeDefinitionId, string key, string name) : base()
    {
        TenantId = tenantId;
        AttributeDefinitionId = attributeDefinitionId;
        Key = key.Require();
        Name = name.Require();
    }

    public Guid TenantId { get; private set; }

    public Guid AttributeDefinitionId { get; private set; }

    public AttributeDefinition AttributeDefinition { get; private set; } = null!;

    public string Key { get; private set; } = null!;

    public string Name { get; private set; } = null!;

    public void Update(string name, bool isActive)
    {
        Name = name.Require();
        SetActive(isActive);
    }
}
