namespace CollectionPlatform.Infrastructure.Services;

using Application;
using Domain.Entities;
using Domain.Enums;
using Microsoft.EntityFrameworkCore;


public sealed class AttributeService(CollectionPlatformDbContext dbContext, ITenantContext tenantContext) : IAttributeService
{
    public async Task<AttributeResponse> CreateAsync(CreateAttributeRequest request, CancellationToken cancellationToken)
    {
        var tenantId = RequireTenant();
        if (request.IsMultiple && request.DataType != AttributeDataType.MultiEnumeration)
        {
            throw new ArgumentException("Only MultiEnumeration attributes can be multiple.", nameof(request));
        }

        var attribute = new AttributeDefinition(tenantId, request.Key, request.Name, request.DataType, request.IsRequired, request.IsMultiple, request.IsFilterable);
        await dbContext.AttributeDefinitions.AddAsync(attribute);
        await dbContext.SaveChangesAsync(cancellationToken);
        return ToResponse(attribute);
    }

    public async Task<IReadOnlyList<AttributeResponse>> GetAsync(CancellationToken cancellationToken)
        => await dbContext.AttributeDefinitions
        .AsNoTracking()
        .Include(attribute => attribute.Values)
        .OrderBy(attribute => attribute.Key)
        .Select(attribute => ToResponse(attribute))
        .ToListAsync(cancellationToken);

    public async Task<AttributeResponse?> GetAsync(Guid id, CancellationToken cancellationToken)
        => await dbContext.AttributeDefinitions
        .AsNoTracking()
        .Include(attribute => attribute.Values)
        .Where(attribute => attribute.Id == id)
        .Select(attribute => ToResponse(attribute))
        .SingleOrDefaultAsync(cancellationToken);

    public async Task<AttributeResponse?> UpdateAsync(Guid id, UpdateAttributeRequest request, CancellationToken cancellationToken)
    {
        var attribute = await dbContext.AttributeDefinitions
            .Include(candidate => candidate.Values)
            .SingleOrDefaultAsync(candidate => candidate.Id == id, cancellationToken);

        if (attribute is null) return null;

        attribute.Update(request.Name, request.IsRequired, request.IsMultiple, request.IsFilterable, request.IsActive);
        await dbContext.SaveChangesAsync(cancellationToken);
        return ToResponse(attribute);
    }

    public async Task<bool> DeactivateAsync(Guid id, CancellationToken cancellationToken)
    {
        var attribute = await dbContext.AttributeDefinitions.SingleOrDefaultAsync(candidate => candidate.Id == id, cancellationToken);
        
        if (attribute is null) return false;
        
        attribute.Update(attribute.Name, attribute.IsRequired, attribute.IsMultiple, attribute.IsFilterable, false);
        await dbContext.SaveChangesAsync(cancellationToken);
        return true;
    }

    public async Task<AttributeValueResponse> CreateValueAsync(Guid attributeId, CreateAttributeValueRequest request, CancellationToken cancellationToken)
    {
        var attribute = await dbContext.AttributeDefinitions
            .SingleOrDefaultAsync(candidate => candidate.Id == attributeId, cancellationToken)
            ?? throw new KeyNotFoundException("Attribute definition was not found.");
        if (attribute.DataType is not (AttributeDataType.Enumeration or AttributeDataType.MultiEnumeration))
        {
            throw new ArgumentException("Only enumeration attributes can have predefined values.", nameof(attributeId));
        }

        var value = new AttributeValue(attribute.TenantId, attribute.Id, request.Key, request.Name);
        await dbContext.AttributeValues.AddAsync(value);
        await dbContext.SaveChangesAsync(cancellationToken);
        return ToResponse(value);
    }

    public async Task<IReadOnlyList<AttributeValueResponse>?> GetValuesAsync(Guid attributeId, CancellationToken cancellationToken)
    {
        if (!await dbContext.AttributeDefinitions.AnyAsync(attribute => attribute.Id == attributeId, cancellationToken)) return null;
        return await dbContext.AttributeValues
            .AsNoTracking()
            .Where(value => value.AttributeDefinitionId == attributeId)
            .OrderBy(value => value.Key)
            .Select(value => ToResponse(value))
            .ToListAsync(cancellationToken);
    }

    public async Task<AttributeValueResponse?> UpdateValueAsync(Guid attributeId, Guid valueId, UpdateAttributeValueRequest request, CancellationToken cancellationToken)
    {
        var value = await dbContext.AttributeValues.SingleOrDefaultAsync(candidate => candidate.Id == valueId && candidate.AttributeDefinitionId == attributeId, cancellationToken);
        
        if (value is null) return null;
        
        value.Update(request.Name, request.IsActive);
        await dbContext.SaveChangesAsync(cancellationToken);
        return ToResponse(value);
    }

    public async Task<bool> DeactivateValueAsync(Guid attributeId, Guid valueId, CancellationToken cancellationToken)
    {
        var value = await dbContext.AttributeValues.SingleOrDefaultAsync(candidate => candidate.Id == valueId && candidate.AttributeDefinitionId == attributeId, cancellationToken);
        
        if (value is null) return false;

        value.Update(value.Name, false);
        await dbContext.SaveChangesAsync(cancellationToken);
        return true;
    }

    private Guid RequireTenant() => tenantContext.TenantId ?? throw new InvalidOperationException("An authenticated tenant is required.");
    private static AttributeResponse ToResponse(AttributeDefinition attribute) => new(attribute.Id, attribute.Key, attribute.Name, attribute.DataType, attribute.IsRequired, attribute.IsMultiple, attribute.IsFilterable, attribute.IsActive, attribute.Values.Select(ToResponse).ToArray());
    private static AttributeValueResponse ToResponse(AttributeValue value) => new(value.Id, value.Key, value.Name, value.IsActive);
}
