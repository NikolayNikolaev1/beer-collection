namespace CollectionPlatform.Infrastructure.Configurations;

using Domain.Entities;
using Extensions;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

internal sealed class CollectionAcquisitionConfiguration : IEntityTypeConfiguration<CollectionAcquisition>
{
    public void Configure(EntityTypeBuilder<CollectionAcquisition> builder)
    {
        builder.HasKey(entity => entity.Id);
        builder.ConfigureTenantOwned();
        builder.Property(entity => entity.ExternalEventId).HasMaxLength(200).IsRequired();
        builder.Property(entity => entity.Source).HasMaxLength(200);
        builder.HasIndex(entity => new { entity.TenantId, entity.ExternalEventId }).IsUnique();
        builder.HasIndex(entity => new { entity.TenantId, entity.UserId, entity.ItemId });
    }
}
