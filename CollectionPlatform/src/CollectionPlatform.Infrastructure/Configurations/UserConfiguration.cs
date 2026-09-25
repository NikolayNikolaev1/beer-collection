namespace CollectionPlatform.Infrastructure.Configurations;

using Domain.Entities;
using Extensions;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

internal sealed class UserConfiguration : IEntityTypeConfiguration<User>
{
    public void Configure(EntityTypeBuilder<User> user)
    {
        user.HasKey(entity => entity.Id);
        user.ConfigureTenantOwned();
        user.Property(entity => entity.ExternalId).HasMaxLength(200).IsRequired();
        user.Property(entity => entity.DisplayName).HasMaxLength(300);
        user.HasIndex(entity => new { entity.TenantId, entity.ExternalId }).IsUnique();

        user.HasOne(entity => entity.Tenant)
            .WithMany(entity => entity.Users)
            .HasForeignKey(entity => entity.TenantId)
            .OnDelete(DeleteBehavior.Restrict);
    }
}
