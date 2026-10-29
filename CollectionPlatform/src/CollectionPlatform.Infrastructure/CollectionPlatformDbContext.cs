namespace CollectionPlatform.Infrastructure;

using Application;
using Domain.Entities;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;

public sealed class CollectionPlatformDbContext(
    DbContextOptions<CollectionPlatformDbContext> options,
    ITenantContext tenantContext) : IdentityDbContext<
        Tenant,
        IdentityRole<Guid>,
        Guid,
        IdentityUserClaim<Guid>,
        IdentityUserRole<Guid>,
        IdentityUserLogin<Guid>,
        IdentityRoleClaim<Guid>,
        IdentityUserToken<Guid>>(options)
{
    public DbSet<Tenant> Tenants => Set<Tenant>();
    public DbSet<ApiKey> ApiKeys => Set<ApiKey>();
    public DbSet<User> Users => Set<User>();
    public DbSet<Item> Items => Set<Item>();
    public DbSet<AttributeDefinition> AttributeDefinitions => Set<AttributeDefinition>();
    public DbSet<AttributeValue> AttributeValues => Set<AttributeValue>();
    public DbSet<ItemAttributeValue> ItemAttributeValues => Set<ItemAttributeValue>();
    public DbSet<Collection> Collections => Set<Collection>();
    public DbSet<CollectionItem> CollectionItems => Set<CollectionItem>();
    public DbSet<CollectionAcquisition> CollectionAcquisitions => Set<CollectionAcquisition>();
    public DbSet<RefreshToken> RefreshTokens => Set<RefreshToken>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);
        modelBuilder.ApplyConfigurationsFromAssembly(typeof(CollectionPlatformDbContext).Assembly);

        modelBuilder.Entity<RefreshToken>().HasQueryFilter(entity => entity.TenantId == tenantContext.TenantId);
        modelBuilder.Entity<ApiKey>().HasQueryFilter(entity => entity.TenantId == tenantContext.TenantId);
        modelBuilder.Entity<User>().HasQueryFilter(entity => entity.TenantId == tenantContext.TenantId);
        modelBuilder.Entity<Item>().HasQueryFilter(entity => entity.TenantId == tenantContext.TenantId);
        modelBuilder.Entity<AttributeDefinition>().HasQueryFilter(entity => entity.TenantId == tenantContext.TenantId);
        modelBuilder.Entity<AttributeValue>().HasQueryFilter(entity => entity.TenantId == tenantContext.TenantId);
        modelBuilder.Entity<ItemAttributeValue>().HasQueryFilter(entity => entity.TenantId == tenantContext.TenantId);
        modelBuilder.Entity<Collection>().HasQueryFilter(entity => entity.TenantId == tenantContext.TenantId);
        modelBuilder.Entity<CollectionItem>().HasQueryFilter(entity => entity.TenantId == tenantContext.TenantId);
        modelBuilder.Entity<CollectionAcquisition>().HasQueryFilter(entity => entity.TenantId == tenantContext.TenantId);
    }
}
