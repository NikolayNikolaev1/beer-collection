namespace CollectionPlatform.Infrastructure.Configurations;

using Domain.Entities;
using Extensions;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

internal sealed class CollectionItemConfiguration : IEntityTypeConfiguration<CollectionItem>
{
    public void Configure(EntityTypeBuilder<CollectionItem> collectionItem)
    {
        collectionItem.HasKey(entity => entity.Id);
        collectionItem.ConfigureTenantOwned();
        collectionItem.Property(entity => entity.Quantity).IsRequired();
        
        collectionItem
            .HasIndex(entity => new { entity.CollectionId, entity.ItemId })
            .IsUnique();
        
        collectionItem
            .HasOne(entity => entity.Collection)
            .WithMany(entity => entity.Items)
            .HasForeignKey(entity => entity.CollectionId)
            .OnDelete(DeleteBehavior.Restrict);
        
        collectionItem
            .HasOne(entity => entity.Item)
            .WithMany(entity => entity.CollectionItems)
            .HasForeignKey(entity => entity.ItemId)
            .OnDelete(DeleteBehavior.Restrict);
    }
}
