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

Run the test suite:

```powershell
dotnet test CollectionPlatform.slnx
```

The integration suite creates a unique SQL Server LocalDB database per fixture, calls `EnsureCreated` for the test schema, and deletes the database afterward. It covers tenant query isolation, API-key revocation, item synchronization/validation, and collection acquisition idempotency. Production deployment remains responsible for its database lifecycle; no migration files are generated here.

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

Synchronize a generic item with typed attributes:

```http
POST /api/v1/items
Content-Type: application/json

{
  "externalId": "beer-123",
  "name": "Chimay Blue",
  "attributes": {
    "country": "BE",
    "alcohol": 9.0
  }
}
```

Bulk synchronization accepts at most 500 records and partially succeeds. Valid records are committed independently; invalid records are returned in `errors` without rolling back other records:

```http
POST /api/v1/items/bulk
Content-Type: application/json

{
  "items": [
    {
      "externalId": "beer-123",
      "name": "Chimay Blue",
      "attributes": { "country": "BE" }
    }
  ]
}
```

The user equivalent is `POST /api/v1/users/bulk`. Both responses include `total`, `created`, `updated`, `unchanged`, `failed`, successful `results`, and per-record `errors`.

When an order is fulfilled, record a retry-safe acquisition:

```http
POST /api/v1/collections/acquisitions
Content-Type: application/json

{
  "userExternalId": "user-123",
  "itemExternalId": "beer-123",
  "externalEventId": "order-10025-line-1",
  "quantity": 1,
  "source": "order"
}
```

Repeating the same `externalEventId` is a no-op. A new event identifier increments the current quantity. Retrieve the collection through `GET /api/v1/users/{externalUserId}/collection?page=1&pageSize=50`.

Attribute definitions and values are tenant-scoped. Unknown attributes, invalid typed values, inactive definitions, and inactive enumeration values are rejected. Bulk synchronization and SQL Server integration fixtures remain next in the implementation sequence.

## Architecture decisions

Every tenant-owned record carries a tenant ID. EF Core global query filters provide the default isolation boundary, while services, relationships, unique indexes, and integration tests reinforce it. Tenant IDs are never accepted from public integration payloads.

Users and items retain both platform-generated internal IDs and tenant-scoped external IDs. Enumeration values use stable keys. Typed item attribute rows are relational so future collection rules can query equality, conjunctions, numeric comparisons, and distinct-value counts without parsing JSON.

Collection acquisition will use an external event identifier for retry-safe idempotency. Current ownership is separate from acquisition history so future order/event features do not require redesigning the collection model.
