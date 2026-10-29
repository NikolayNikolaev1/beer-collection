namespace CollectionPlatform.Infrastructure.Configurations;

using Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

internal sealed class TenantConfiguration : IEntityTypeConfiguration<Tenant>
{
    public void Configure(EntityTypeBuilder<Tenant> builder)
    {
        builder.HasKey(entity => entity.Id);
        builder.Property(entity => entity.UserName).HasMaxLength(256);
        builder.Property(entity => entity.NormalizedUserName).HasMaxLength(256);
        builder.Property(entity => entity.Email).HasMaxLength(256);
        builder.Property(entity => entity.NormalizedEmail).HasMaxLength(256);
        builder.Property(entity => entity.Name).HasMaxLength(200).IsRequired();
        builder.Property(entity => entity.ExternalId).HasMaxLength(200);
        builder.HasIndex(entity => entity.ExternalId).IsUnique().HasFilter("[ExternalId] IS NOT NULL");
        builder.Property(entity => entity.CreatedAt).IsRequired();
        builder.Property(entity => entity.UpdatedAt).IsRequired();
        builder.HasIndex(entity => entity.NormalizedEmail).IsUnique().HasFilter("[NormalizedEmail] IS NOT NULL");
        builder.HasIndex(entity => entity.NormalizedUserName).IsUnique().HasFilter("[NormalizedUserName] IS NOT NULL");
    }
}
