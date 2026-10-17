namespace CollectionPlatform.Domain.Entities;

using Common.Extensions;
using Enums;

public sealed class AttributeDefinition : AuditEntity
{
    private AttributeDefinition() { }

    public AttributeDefinition(
        Guid tenantId,
        string key,
        string name,
        AttributeDataType dataType,
        bool isRequired,
        bool isMultiple,
        bool isFilterable) : base()
    {
        TenantId = tenantId;
        Key = key.Require().ToLowerInvariant();
        Name = name.Require();
        DataType = dataType;
        IsRequired = isRequired;
        IsMultiple = isMultiple;
        IsFilterable = isFilterable;
    }

    public Guid TenantId { get; private set; }

    public Tenant Tenant { get; private set; } = null!;

    public string Key { get; private set; } = null!;

    public string Name { get; private set; } = null!;

    public AttributeDataType DataType { get; private set; }

    public bool IsRequired { get; private set; }

    public bool IsMultiple { get; private set; }

    public bool IsFilterable { get; private set; }

    public List<AttributeValue> Values { get; private set; } = [];

    public List<ItemAttributeValue> ItemValues { get; private set; } = [];

    public void Update(string name, bool isRequired, bool isMultiple, bool isFilterable, bool isActive)
    {
        Name = name.Require();
        IsRequired = isRequired;
        IsMultiple = isMultiple;
        IsFilterable = isFilterable;
        SetActive(isActive);
    }
}
