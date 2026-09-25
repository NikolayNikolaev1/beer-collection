namespace CollectionPlatform.Infrastructure.Configurations;

using Domain.Entities;
using Extensions;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

internal sealed class ApiKeyConfiguration : IEntityTypeConfiguration<ApiKey>
{
    public void Configure(EntityTypeBuilder<ApiKey> apiKey)
    {
        apiKey.HasKey(entity => entity.Id);
        apiKey.ConfigureTenantOwned();
        apiKey.Property(entity => entity.Name).HasMaxLength(200).IsRequired();
        apiKey.Property(entity => entity.KeyPrefix).HasMaxLength(16).IsRequired();
        apiKey.Property(entity => entity.KeyHash).HasMaxLength(128).IsRequired();
        apiKey.HasIndex(entity => new { entity.TenantId, entity.KeyPrefix }).IsUnique();

        apiKey
            .HasOne(entity => entity.Tenant)
            .WithMany(entity => entity.ApiKeys)
            .HasForeignKey(entity => entity.TenantId)
            .OnDelete(DeleteBehavior.Restrict);
    }
}
