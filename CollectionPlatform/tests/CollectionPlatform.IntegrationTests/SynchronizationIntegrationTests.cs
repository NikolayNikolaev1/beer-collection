using System.Text.Json;
using CollectionPlatform.Application;
using CollectionPlatform.Domain.Entities;
using CollectionPlatform.Domain.Enums;
using CollectionPlatform.Infrastructure;
using CollectionPlatform.Infrastructure.Services;

namespace CollectionPlatform.IntegrationTests;

public sealed class SynchronizationIntegrationTests(SqlServerFixture fixture) : IClassFixture<SqlServerFixture>
{
    [Fact]
    public async Task TenantFilterPreventsCrossTenantUserReads()
    {
        var tenantA = await fixture.CreateTenantAsync("Tenant A");
        var tenantB = await fixture.CreateTenantAsync("Tenant B");
        await using (var db = fixture.CreateContext(tenantA.Context))
        {
            var service = new UserSynchronizationService(db, tenantA.Context);
            await service.SynchronizeAsync(new SynchronizeUserRequest("same-user", "Tenant A"), CancellationToken.None);
        }

        await using (var db = fixture.CreateContext(tenantB.Context))
        {
            var service = new UserSynchronizationService(db, tenantB.Context);
            var users = await service.GetAsync(1, 50, CancellationToken.None);
            Assert.Empty(users);
        }
    }

    [Fact]
    public async Task ItemSynchronizationIsIdempotentAndRejectsUnknownAttributes()
    {
        var tenant = await fixture.CreateTenantAsync("Items");
        await using var db = fixture.CreateContext(tenant.Context);
        var attributes = new AttributeService(db, tenant.Context);
        await attributes.CreateAsync(new CreateAttributeRequest("country", "Country", AttributeDataType.Text), CancellationToken.None);
        var service = new ItemSynchronizationService(db, tenant.Context);
        var request = new SynchronizeItemRequest("item-1", "Example", null, new Dictionary<string, JsonElement>
        {
            ["country"] = JsonSerializer.SerializeToElement("BE")
        });

        var created = await service.SynchronizeAsync(request, CancellationToken.None);
        var unchanged = await service.SynchronizeAsync(request, CancellationToken.None);

        Assert.Equal(SynchronizationStatus.Created, created.Status);
        Assert.Equal(SynchronizationStatus.Unchanged, unchanged.Status);
        await Assert.ThrowsAsync<ArgumentException>(() => service.SynchronizeAsync(request with
        {
            Attributes = new Dictionary<string, JsonElement>
            {
                ["unknown"] = JsonSerializer.SerializeToElement("value")
            }
        }, CancellationToken.None));
    }

    [Fact]
    public async Task CollectionAcquisitionRetryDoesNotIncreaseQuantity()
    {
        var tenant = await fixture.CreateTenantAsync("Collections");
        await using var db = fixture.CreateContext(tenant.Context);
        var users = new UserSynchronizationService(db, tenant.Context);
        var items = new ItemSynchronizationService(db, tenant.Context);
        await users.SynchronizeAsync(new SynchronizeUserRequest("user-1", "User"), CancellationToken.None);
        await items.SynchronizeAsync(new SynchronizeItemRequest("item-1", "Item", null), CancellationToken.None);
        var collections = new CollectionService(db, tenant.Context);
        var request = new AcquireCollectionItemRequest("user-1", "item-1", "event-1", 2);

        var first = await collections.AcquireAsync(request, CancellationToken.None);
        var retry = await collections.AcquireAsync(request, CancellationToken.None);

        Assert.Equal("Created", first.Status);
        Assert.Equal("Unchanged", retry.Status);
        Assert.Equal(2, retry.Item.Quantity);
    }

    [Fact]
    public async Task ApiKeyValidatorRejectsRevokedKeys()
    {
        var tenant = await fixture.CreateTenantAsync("Authentication");
        await using var db = fixture.CreateContext(tenant.Context);
        var generated = ApiKeyGenerator.Create();
        var key = new ApiKey(tenant.Tenant.Id, "test", generated.Prefix, generated.Hash);
        await db.ApiKeys.AddAsync(key);
        await db.SaveChangesAsync();
        var validator = new ApiKeyValidator(db);
        var valid = await validator.ValidateAsync(generated.Credential, CancellationToken.None);
        Assert.NotNull(valid);

        key.Revoke();
        await db.SaveChangesAsync();

        var revoked = await validator.ValidateAsync(generated.Credential, CancellationToken.None);
        Assert.Null(revoked);
    }
}
