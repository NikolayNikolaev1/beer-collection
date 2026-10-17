namespace CollectionPlatform.Domain.Entities;

public abstract class AuditEntity
{
    protected AuditEntity()
    {
        Id = Guid.NewGuid();
        IsActive = true;
        CreatedAt = UpdatedAt = DateTimeOffset.UtcNow;
    }

    public Guid Id { get; protected set; }

    public bool IsActive { get; protected set; }

    public DateTimeOffset CreatedAt { get; protected set; }

    public DateTimeOffset UpdatedAt { get; protected set; }

    public virtual void Deactivate()
    {
        IsActive = false;
        UpdatedAt = DateTimeOffset.UtcNow;
    }

    protected virtual void Update()
    {
        IsActive = true;
        UpdatedAt = DateTimeOffset.UtcNow;
    }

    protected void SetActive(bool isActive)
    {
        IsActive = isActive;
        UpdatedAt = DateTimeOffset.UtcNow;
    }
}
