namespace CollectionPlatform.Infrastructure.Configurations;

using Domain.Entities;
using Extensions;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

internal sealed class ItemAttributeValueConfiguration : IEntityTypeConfiguration<ItemAttributeValue>
{
    public void Configure(EntityTypeBuilder<ItemAttributeValue> builder)
    {
        builder.HasKey(entity => entity.Id);
        builder.ConfigureTenantOwned();
        builder.Property(entity => entity.TextValue).HasMaxLength(4000);
        builder.Property(entity => entity.DecimalValue).HasPrecision(18, 6);
        builder.HasIndex(entity => new { entity.TenantId, entity.AttributeDefinitionId, entity.AttributeValueId });
        builder.HasIndex(entity => new { entity.TenantId, entity.ItemId, entity.AttributeDefinitionId });
        
        builder
            .HasOne(entity => entity.Item)
            .WithMany(entity => entity.AttributeValues)
            .HasForeignKey(entity => entity.ItemId)
            .OnDelete(DeleteBehavior.Restrict);
        
        builder
            .HasOne(entity => entity.AttributeDefinition)
            .WithMany(entity => entity.ItemValues)
            .HasForeignKey(entity => entity.AttributeDefinitionId)
            .OnDelete(DeleteBehavior.Restrict);
        
        builder
            .HasOne(entity => entity.AttributeValue)
            .WithMany()
            .HasForeignKey(entity => entity.AttributeValueId)
            .OnDelete(DeleteBehavior.Restrict);
    }
}
