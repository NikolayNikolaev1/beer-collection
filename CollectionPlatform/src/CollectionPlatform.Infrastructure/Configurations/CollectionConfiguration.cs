namespace CollectionPlatform.Infrastructure.Configurations;

using Domain.Entities;
using Extensions;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

internal sealed class CollectionConfiguration : IEntityTypeConfiguration<Collection>
{
    public void Configure(EntityTypeBuilder<Collection> collection)
    {
        collection.HasKey(entity => entity.Id);
        collection.ConfigureTenantOwned();
        collection.HasIndex(entity => new { entity.TenantId, entity.UserId }).IsUnique();
        
        collection
            .HasOne(entity => entity.User)
            .WithOne(entity => entity.Collection)
            .HasForeignKey<Collection>(entity => entity.UserId)
            .OnDelete(DeleteBehavior.Restrict);
    }
}
