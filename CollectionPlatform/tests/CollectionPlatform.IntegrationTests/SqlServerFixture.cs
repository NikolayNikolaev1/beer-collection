using CollectionPlatform.Application;
using CollectionPlatform.Domain.Entities;
using CollectionPlatform.Infrastructure;
using Microsoft.EntityFrameworkCore;

namespace CollectionPlatform.IntegrationTests;

public sealed class SqlServerFixture : IAsyncLifetime
{
    public string DatabaseName { get; } = $"CollectionPlatformTests_{Guid.NewGuid():N}";
    public string ConnectionString => $"Server=(localdb)\\MSSQLLocalDB;Database={DatabaseName};Trusted_Connection=True;TrustServerCertificate=True;MultipleActiveResultSets=True";

    public async Task InitializeAsync()
    {
        await using var db = CreateContext(new TenantContext());
        await db.Database.EnsureCreatedAsync();
    }

    public async Task DisposeAsync()
    {
        await using var db = CreateContext(new TenantContext());
        await db.Database.EnsureDeletedAsync();
    }

    public CollectionPlatformDbContext CreateContext(TenantContext tenantContext)
    {
        var options = new DbContextOptionsBuilder<CollectionPlatformDbContext>()
            .UseSqlServer(ConnectionString)
            .Options;
        return new CollectionPlatformDbContext(options, tenantContext);
    }

    public async Task<(Tenant Tenant, TenantContext Context)> CreateTenantAsync(string name)
    {
        var context = new TenantContext();
        await using var db = CreateContext(context);
        var tenant = new Tenant(name);
        await db.Tenants.AddAsync(tenant);
        await db.SaveChangesAsync();
        context.Set(tenant.Id, Guid.NewGuid());
        return (tenant, context);
    }
}
