namespace CollectionPlatform.Domain.Entities;

using Common.Extensions;
using Microsoft.AspNetCore.Identity;

public sealed class Tenant : IdentityUser<Guid>
{
    private Tenant() { }

    public Tenant(string name, string? externalId = null)
    {
        Id = Guid.NewGuid();
        Name = name.Require();
        ExternalId = string.IsNullOrWhiteSpace(externalId) ? null : externalId.Trim();
        IsActive = true;
        CreatedAt = UpdatedAt = DateTimeOffset.UtcNow;
    }

    public string Name { get; private set; } = null!;

    public string? ExternalId { get; private set; }

    public bool IsActive { get; private set; }

    public DateTimeOffset CreatedAt { get; private set; }

    public DateTimeOffset UpdatedAt { get; private set; }

    public void UpdateProfile(string name, string? email)
    {
        Name = name.Require();
        Email = string.IsNullOrWhiteSpace(email) ? null : email.Trim();
        UserName = Email;
        NormalizedEmail = Email?.ToUpperInvariant();
        NormalizedUserName = UserName?.ToUpperInvariant();
        UpdatedAt = DateTimeOffset.UtcNow;
    }

    public override string ToString() => Name;

    public List<ApiKey> ApiKeys { get; private set; } = [];

    public IEnumerable<User> Users { get; private set; } = [];

    public IEnumerable<Item> Items { get; private set; } = [];

    public IEnumerable<AttributeDefinition> AttributeDefinitions { get; private set; } = [];
}
