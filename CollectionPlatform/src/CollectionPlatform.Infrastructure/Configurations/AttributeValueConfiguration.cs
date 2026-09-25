namespace CollectionPlatform.Infrastructure.Configurations;

using Domain.Entities;
using Extensions;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

internal sealed class AttributeValueConfiguration : IEntityTypeConfiguration<AttributeValue>
{
    public void Configure(EntityTypeBuilder<AttributeValue> attrValue)
    {
        attrValue.HasKey(entity => entity.Id);
        attrValue.ConfigureTenantOwned();
        attrValue.Property(entity => entity.Key).HasMaxLength(100).IsRequired();
        attrValue.Property(entity => entity.Name).HasMaxLength(200).IsRequired();
        attrValue.HasIndex(entity => new { entity.AttributeDefinitionId, entity.Key }).IsUnique();

        attrValue
            .HasOne(entity => entity.AttributeDefinition)
            .WithMany(entity => entity.Values)
            .HasForeignKey(entity => entity.AttributeDefinitionId)
            .OnDelete(DeleteBehavior.Restrict);
    }
}
