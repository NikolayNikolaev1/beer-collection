namespace CollectionPlatform.Domain.Entities;

public sealed class RefreshToken
{
    private RefreshToken() { }

    public RefreshToken(Guid tenantId, string tokenHash, DateTimeOffset expiresAt)
    {
        Id = Guid.NewGuid();
        TenantId = tenantId;
        TokenHash = tokenHash;
        ExpiresAt = expiresAt;
        CreatedAt = DateTimeOffset.UtcNow;
    }

    public Guid Id { get; private set; }
    public Guid TenantId { get; private set; }
    public Tenant Tenant { get; private set; } = null!;
    public string TokenHash { get; private set; } = null!;
    public DateTimeOffset ExpiresAt { get; private set; }
    public DateTimeOffset CreatedAt { get; private set; }
    public DateTimeOffset? RevokedAt { get; private set; }
    public Guid? ReplacedById { get; private set; }

    public bool IsActive => RevokedAt is null && ExpiresAt > DateTimeOffset.UtcNow;

    public void Revoke(Guid? replacedById = null)
    {
        RevokedAt ??= DateTimeOffset.UtcNow;
        ReplacedById = replacedById;
    }
}