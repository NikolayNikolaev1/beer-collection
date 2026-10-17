namespace CollectionPlatform.Infrastructure.Services;

using Application;
using Common.Extensions;
using Domain.Entities;
using Microsoft.EntityFrameworkCore;

public sealed class CollectionService(
    CollectionPlatformDbContext dbContext,
    ITenantContext tenantContext) : ICollectionService
{
    public async Task<CollectionAcquisitionResponse> AcquireAsync(AcquireCollectionItemRequest request, CancellationToken cancellationToken)
    {
        var tenantId = tenantContext.TenantId ?? throw new InvalidOperationException("An authenticated tenant is required.");
        var userExternalId = request.UserExternalId.Require();
        var itemExternalId = request.ItemExternalId.Require();
        var eventId = request.ExternalEventId.Require();

        await using var transaction = await dbContext.Database.BeginTransactionAsync(cancellationToken);
        var user = await dbContext.Users.SingleOrDefaultAsync(candidate => candidate.ExternalId == userExternalId && candidate.IsActive, cancellationToken)
            ?? throw new KeyNotFoundException("User was not found or is inactive.");
        var item = await dbContext.Items.SingleOrDefaultAsync(candidate => candidate.ExternalId == itemExternalId && candidate.IsActive, cancellationToken)
            ?? throw new KeyNotFoundException("Item was not found or is inactive.");
        var existingEvent = await dbContext.CollectionAcquisitions.SingleOrDefaultAsync(candidate => candidate.ExternalEventId == eventId, cancellationToken);

        var collection = await dbContext.Collections.SingleOrDefaultAsync(candidate => candidate.UserId == user.Id, cancellationToken);
        if (collection is null)
        {
            collection = new Collection(tenantId, user.Id);
            await dbContext.Collections.AddAsync(collection, cancellationToken);
        }

        var collectionItem = await dbContext.CollectionItems
            .Include(candidate => candidate.Item).ThenInclude(candidate => candidate.AttributeValues).ThenInclude(candidate => candidate.AttributeDefinition)
            .Include(candidate => candidate.Item).ThenInclude(candidate => candidate.AttributeValues).ThenInclude(candidate => candidate.AttributeValue)
            .SingleOrDefaultAsync(candidate => candidate.CollectionId == collection.Id && candidate.ItemId == item.Id, cancellationToken);

        var status = "Unchanged";
        if (existingEvent is null)
        {
            var acquisition = new CollectionAcquisition(tenantId, eventId, user.Id, item.Id, request.Quantity, request.Source);
            await dbContext.CollectionAcquisitions.AddAsync(acquisition, cancellationToken);
            if (collectionItem is null)
            {
                collectionItem = new CollectionItem(tenantId, collection.Id, item.Id, request.Quantity);
                await dbContext.CollectionItems.AddAsync(collectionItem, cancellationToken);
            }
            else
            {
                collectionItem.AddQuantity(request.Quantity);
            }
            status = "Created";
        }

        await dbContext.SaveChangesAsync(cancellationToken);
        await transaction.CommitAsync(cancellationToken);
        return new CollectionAcquisitionResponse(eventId, status, ToResponse(collectionItem!, item));
    }

    public async Task<CollectionResponse?> GetAsync(string userExternalId, int page, int pageSize, CancellationToken cancellationToken)
    {
        var normalizedExternalId = userExternalId.Require();
        var user = await dbContext.Users
            .AsNoTracking()
            .SingleOrDefaultAsync(candidate => candidate.ExternalId == normalizedExternalId, cancellationToken);

        if (user is null) return null;

        var collection = await dbContext.Collections
            .AsNoTracking()
            .Where(candidate => candidate.UserId == user.Id)
            .SelectMany(candidate => candidate.Items)
            .Include(candidate => candidate.Item).ThenInclude(candidate => candidate.AttributeValues).ThenInclude(candidate => candidate.AttributeDefinition)
            .Include(candidate => candidate.Item).ThenInclude(candidate => candidate.AttributeValues).ThenInclude(candidate => candidate.AttributeValue)
            .OrderBy(candidate => candidate.ItemId)
            .Skip((page - 1) * pageSize)
            .Take(pageSize)
            .ToListAsync(cancellationToken);

        return new CollectionResponse(normalizedExternalId, collection.Select(candidate => ToResponse(candidate, candidate.Item)).ToArray(), page, pageSize);
    }

    private static CollectionItemResponse ToResponse(CollectionItem collectionItem, Item item)
        => new(item.Id, item.ExternalId, item.Name, collectionItem.Quantity, item.IsActive, item.AttributeValues.Select(attribute =>
            new ItemAttributeResponse(attribute.AttributeDefinition.Key, attribute.AttributeDefinition.DataType, attribute.AttributeValue?.Key ?? (object?)attribute.TextValue ?? attribute.IntegerValue?.ToString() ?? attribute.DecimalValue?.ToString() ?? attribute.BooleanValue?.ToString() ?? attribute.DateValue?.ToString())).ToArray());
}
