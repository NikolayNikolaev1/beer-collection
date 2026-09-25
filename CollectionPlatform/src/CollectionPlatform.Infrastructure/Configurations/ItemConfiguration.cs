namespace CollectionPlatform.Infrastructure.Configurations;

using Domain.Entities;
using Extensions;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

internal sealed class ItemConfiguration : IEntityTypeConfiguration<Item>
{
    public void Configure(EntityTypeBuilder<Item> item)
    {
        item.HasKey(entity => entity.Id);
        item.ConfigureTenantOwned();
        item.Property(entity => entity.ExternalId).HasMaxLength(200).IsRequired();
        item.Property(entity => entity.Name).HasMaxLength(300).IsRequired();
        item.Property(entity => entity.Description).HasMaxLength(4000);
        item.HasIndex(entity => new { entity.TenantId, entity.ExternalId }).IsUnique();

        item.HasOne(entity => entity.Tenant)
            .WithMany(entity => entity.Items)
            .HasForeignKey(entity => entity.TenantId)
            .OnDelete(DeleteBehavior.Restrict);
    }
}
