# Collection Platform

Collection Platform is a domain-independent, multi-tenant Collection-as-a-Service API. Tenants define their own item vocabulary through custom attributes; the platform does not contain beer-, trading-card-, or other domain-specific fields.

## Stack

- .NET 10 / ASP.NET Core Web API
- EF Core 10 with SQL Server
- API-key authentication through `Authorization: ApiKey <credential>`
- xUnit tests

## Projects

- `CollectionPlatform.Api`: versioned HTTP API and composition root.
- `CollectionPlatform.Application`: contracts and application abstractions.
- `CollectionPlatform.Domain`: platform entities and invariants.
- `CollectionPlatform.Infrastructure`: EF Core SQL Server model, tenant filters, and services.
- `CollectionPlatform.Provisioning`: controlled local/admin bootstrap tool.

## Local setup

Install the .NET 10 SDK and SQL Server LocalDB. The API uses the `ConnectionStrings:DefaultConnection` setting in `src/CollectionPlatform.Api/appsettings.json`; use environment variables or user secrets for deployment-specific values.

Provision a tenant and receive an API key:

```powershell
dotnet run --project src/CollectionPlatform.Provisioning -- "Beer Shop"
```

The provisioning command uses `EnsureCreated` for an empty local database. Production database deployment is intentionally separate and this repository does not contain EF migration files.

Start the API:

```powershell
dotnet run --project src/CollectionPlatform.Api
```

Swagger is available at `/swagger` in development.

## Current API onboarding

Use the provisioned key on every integration request:

```http
Authorization: ApiKey cp..._...
```

Configure an attribute:

```http
POST /api/v1/attributes
Content-Type: application/json

{
  "key": "country",
  "name": "Country",
  "dataType": "Enumeration",
  "isFilterable": true
}
```

Create a value, then synchronize a user:

```http
POST /api/v1/users
Content-Type: application/json

{
  "externalId": "user-123",
  "displayName": "John"
}
```

Attribute definitions and values are tenant-scoped. The next implementation slice adds item synchronization and collection acquisition/retrieval on top of these contracts.

## Architecture decisions

Every tenant-owned record carries a tenant ID. EF Core global query filters provide the default isolation boundary, while services, relationships, unique indexes, and integration tests reinforce it. Tenant IDs are never accepted from public integration payloads.

Users and items retain both platform-generated internal IDs and tenant-scoped external IDs. Enumeration values use stable keys. Typed item attribute rows are relational so future collection rules can query equality, conjunctions, numeric comparisons, and distinct-value counts without parsing JSON.

Collection acquisition will use an external event identifier for retry-safe idempotency. Current ownership is separate from acquisition history so future order/event features do not require redesigning the collection model.
