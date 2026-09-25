namespace CollectionPlatform.Infrastructure.Configurations;

using Domain.Entities;
using Extensions;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

internal sealed class AttributeDefinitionConfiguration : IEntityTypeConfiguration<AttributeDefinition>
{
    public void Configure(EntityTypeBuilder<AttributeDefinition> attribute)
    {
        attribute.HasKey(entity => entity.Id);
        attribute.ConfigureTenantOwned();
        attribute.Property(entity => entity.Key).HasMaxLength(100).IsRequired();
        attribute.Property(entity => entity.Name).HasMaxLength(200).IsRequired();
        attribute.HasIndex(entity => new { entity.TenantId, entity.Key }).IsUnique();

        attribute
            .HasOne(entity => entity.Tenant)
            .WithMany(entity => entity.AttributeDefinitions)
            .HasForeignKey(entity => entity.TenantId)
            .OnDelete(DeleteBehavior.Restrict);
    }
}
