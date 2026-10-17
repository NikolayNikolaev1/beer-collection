using CollectionPlatform.Domain.Entities;

namespace CollectionPlatform.UnitTests;

public sealed class DomainEntityTests
{
    [Fact]
    public void AttributeValueUpdateHonorsActiveState()
    {
        var value = new AttributeValue(Guid.NewGuid(), Guid.NewGuid(), "BE", "Belgium");

        value.Update("Belgium", false);

        Assert.False(value.IsActive);
    }

    [Fact]
    public void AttributeDefinitionUpdateHonorsActiveState()
    {
        var definition = new AttributeDefinition(Guid.NewGuid(), "country", "Country", Domain.Enums.AttributeDataType.Enumeration, false, false, true);

        definition.Update("Country", false, false, true, false);

        Assert.False(definition.IsActive);
    }

    [Fact]
    public void CollectionItemRejectsNonPositiveQuantity()
    {
        Assert.Throws<ArgumentOutOfRangeException>(() => new CollectionItem(Guid.NewGuid(), Guid.NewGuid(), Guid.NewGuid(), 0));
    }

    [Fact]
    public void CollectionItemAddsQuantity()
    {
        var collectionItem = new CollectionItem(Guid.NewGuid(), Guid.NewGuid(), Guid.NewGuid(), 2);

        collectionItem.AddQuantity(3);

        Assert.Equal(5, collectionItem.Quantity);
    }
}
