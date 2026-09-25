namespace CollectionPlatform.Infrastructure.Extensions;

using Domain.Entities;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

internal static class ConfigurationExtensions
{
    public static void ConfigureTenantOwned<TEntity>(this EntityTypeBuilder<TEntity> builder)
        where TEntity : AuditEntity
    {
        builder.Property<Guid>("TenantId").IsRequired();
        builder.HasIndex("TenantId");
    }
}
