using CollectionPlatform.Domain.Entities;
using CollectionPlatform.Infrastructure;
using Microsoft.EntityFrameworkCore;

if (args.Length is < 1 or > 2)
{
    Console.Error.WriteLine("Usage: CollectionPlatform.Provisioning <tenant-name> [connection-string]");
    return 2;
}

var tenantName = args[0];
var connectionString = args.Length == 2
    ? args[1]
    : "Server=(localdb)\\MSSQLLocalDB;Database=CollectionPlatform;Trusted_Connection=True;TrustServerCertificate=True;MultipleActiveResultSets=True";

var tenantContext = new TenantContext();
var options = new DbContextOptionsBuilder<CollectionPlatformDbContext>()
    .UseSqlServer(connectionString)
    .Options;

await using var db = new CollectionPlatformDbContext(options, tenantContext);
await db.Database.EnsureCreatedAsync();

var tenant = new Tenant(tenantName);
var generated = ApiKeyGenerator.Create();
var apiKey = new ApiKey(tenant.Id, "Initial integration key", generated.Prefix, generated.Hash);
db.Tenants.Add(tenant);
db.ApiKeys.Add(apiKey);
await db.SaveChangesAsync();

Console.WriteLine($"Tenant ID: {tenant.Id}");
Console.WriteLine("API key (store this now; it will not be shown again):");
Console.WriteLine(generated.Credential);
return 0;
