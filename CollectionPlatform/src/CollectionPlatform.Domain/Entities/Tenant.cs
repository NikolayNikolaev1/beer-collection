namespace CollectionPlatform.Domain.Entities;

using Common.Extensions;

public sealed class Tenant : AuditEntity
{
    private Tenant() { }

    public Tenant(string name, string? externalId = null) : base()
    {
        Name = name.Require();
        ExternalId = string.IsNullOrWhiteSpace(externalId) ? null : externalId.Trim();
    }

    public string Name { get; private set; } = null!;

    public string? ExternalId { get; private set; }

    public IEnumerable<ApiKey> ApiKeys { get; private set; } = [];

    public IEnumerable<User> Users { get; private set; } = [];

    public IEnumerable<Item> Items { get; private set; } = [];

    public IEnumerable<AttributeDefinition> AttributeDefinitions { get; private set; } = [];
}
