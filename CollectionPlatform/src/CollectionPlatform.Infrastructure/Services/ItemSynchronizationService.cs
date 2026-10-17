namespace CollectionPlatform.Infrastructure.Services;

using System.Globalization;
using System.Text.Json;
using Application;
using Common.Extensions;
using Domain.Entities;
using Domain.Enums;
using Microsoft.EntityFrameworkCore;

public sealed class ItemSynchronizationService(
    CollectionPlatformDbContext dbContext,
    ITenantContext tenantContext) : IItemSynchronizationService
{
    public async Task<(ItemResponse Item, SynchronizationStatus Status)> SynchronizeAsync(
        SynchronizeItemRequest request,
        CancellationToken cancellationToken)
    {
        var tenantId = tenantContext.TenantId ?? throw new InvalidOperationException("An authenticated tenant is required.");
        var externalId = request.ExternalId.Require();
        var attributes = (request.Attributes ?? [])
            .ToDictionary(pair => pair.Key.ToLowerInvariant(), pair => pair.Value, StringComparer.OrdinalIgnoreCase);
        var definitions = await dbContext.AttributeDefinitions
            .Include(definition => definition.Values)
            .ToListAsync(cancellationToken);

        var definitionsByKey = definitions.ToDictionary(definition => definition.Key, StringComparer.OrdinalIgnoreCase);
        foreach (var key in attributes.Keys)
        {
            if (!definitionsByKey.ContainsKey(key))
            {
                throw new ArgumentException($"Unknown attribute: {key}", nameof(request));
            }
        }

        var assignments = ResolveAssignments(attributes, definitionsByKey);
        await using var transaction = await dbContext.Database.BeginTransactionAsync(cancellationToken);
        var item = await dbContext.Items
            .Include(candidate => candidate.AttributeValues)
            .SingleOrDefaultAsync(candidate => candidate.ExternalId == externalId, cancellationToken);
        var status = SynchronizationStatus.Created;

        if (item is null)
        {
            item = new Item(tenantId, externalId, request.Name, request.Description);
            await dbContext.Items.AddAsync(item, cancellationToken);
        }
        else
        {
            status = IsUnchanged(item, request, assignments)
                ? SynchronizationStatus.Unchanged
                : SynchronizationStatus.Updated;
            item.Synchronize(request.Name, request.Description, request.IsActive);
            dbContext.ItemAttributeValues.RemoveRange(item.AttributeValues);
        }

        foreach (var assignment in assignments)
        {
            await dbContext.ItemAttributeValues.AddAsync(assignment.ToEntity(item.Id), cancellationToken);
        }

        await dbContext.SaveChangesAsync(cancellationToken);
        await transaction.CommitAsync(cancellationToken);
        return (ToResponse(item, assignments), status);
    }

    public async Task<BulkSynchronizationResponse<BulkItemResult>> SynchronizeBulkAsync(
        IReadOnlyList<SynchronizeItemRequest> requests,
        CancellationToken cancellationToken)
    {
        var results = new List<BulkItemResult>();
        var errors = new List<BulkRecordError>();
        var created = 0;
        var updated = 0;
        var unchanged = 0;

        foreach (var request in requests)
        {
            try
            {
                var result = await SynchronizeAsync(request, cancellationToken);
                var status = result.Status.ToString();
                results.Add(new BulkItemResult(result.Item, status));
                switch (result.Status)
                {
                    case SynchronizationStatus.Created: created++; break;
                    case SynchronizationStatus.Updated: updated++; break;
                    case SynchronizationStatus.Unchanged: unchanged++; break;
                }
            }
            catch (Exception exception) when (exception is ArgumentException or InvalidOperationException or KeyNotFoundException or DbUpdateException)
            {
                errors.Add(new BulkRecordError(request.ExternalId, GetErrorCode(exception), exception.Message));
            }
        }

        return new BulkSynchronizationResponse<BulkItemResult>(
            requests.Count,
            created,
            updated,
            unchanged,
            errors.Count,
            results,
            errors);
    }

    public async Task<IReadOnlyList<ItemResponse>> GetAsync(int page, int pageSize, bool? active, CancellationToken cancellationToken)
    {
        var query = dbContext.Items.AsNoTracking()
            .Include(item => item.AttributeValues).ThenInclude(value => value.AttributeDefinition)
            .Include(item => item.AttributeValues).ThenInclude(value => value.AttributeValue)
            .AsQueryable();

        if (active.HasValue) query = query.Where(item => item.IsActive == active.Value);

        var items = await query
            .OrderBy(item => item.Id)
            .Skip((page - 1) * pageSize)
            .Take(pageSize)
            .ToListAsync(cancellationToken);

        return items.Select(item => ToResponse(item, item.AttributeValues)).ToArray();
    }

    private static List<ResolvedAssignment> ResolveAssignments(
        IReadOnlyDictionary<string, JsonElement> attributes,
        IReadOnlyDictionary<string, AttributeDefinition> definitions)
    {
        var assignments = new List<ResolvedAssignment>();
        foreach (var pair in attributes)
        {
            var definition = definitions[pair.Key];
            if (!definition.IsActive) throw new ArgumentException($"Attribute '{definition.Key}' is inactive.", nameof(attributes));
            if (definition.DataType == AttributeDataType.MultiEnumeration)
            {
                if (pair.Value.ValueKind != JsonValueKind.Array || pair.Value.GetArrayLength() == 0)
                {
                    throw new ArgumentException($"Invalid value for attribute '{definition.Key}'.", nameof(attributes));
                }

                foreach (var element in pair.Value.EnumerateArray())
                {
                    assignments.Add(ResolveEnumeration(definition, element, true));
                }
            }
            else
            {
                assignments.Add(ResolveScalar(definition, pair.Value));
            }
        }

        foreach (var definition in definitions.Values)
        {
            if (definition.IsRequired && !attributes.ContainsKey(definition.Key))
            {
                throw new ArgumentException($"Required attribute is missing: {definition.Key}", nameof(attributes));
            }
        }

        return assignments;
    }

    private static string GetErrorCode(Exception exception) => exception.Message.StartsWith("Unknown attribute:", StringComparison.Ordinal)
        ? "UNKNOWN_ATTRIBUTE"
        : exception.Message.StartsWith("Invalid value for attribute", StringComparison.Ordinal)
            ? "INVALID_ATTRIBUTE_VALUE"
            : exception.Message.StartsWith("Required attribute is missing:", StringComparison.Ordinal)
                ? "REQUIRED_ATTRIBUTE_MISSING"
                : "ITEM_SYNC_FAILED";

    private static ResolvedAssignment ResolveScalar(AttributeDefinition definition, JsonElement value) => definition.DataType switch
    {
        AttributeDataType.Text when value.ValueKind == JsonValueKind.String => new(definition, value.GetString()!),
        AttributeDataType.Integer when value.TryGetInt64(out var integer) => new(definition, integer),
        AttributeDataType.Decimal when value.TryGetDecimal(out var decimalValue) => new(definition, decimalValue),
        AttributeDataType.Boolean when value.ValueKind is JsonValueKind.True or JsonValueKind.False => new(definition, value.GetBoolean()),
        AttributeDataType.Date when value.ValueKind == JsonValueKind.String && DateOnly.TryParse(value.GetString(), CultureInfo.InvariantCulture, DateTimeStyles.None, out var date) => new(definition, date),
        AttributeDataType.Enumeration => ResolveEnumeration(definition, value, false),
        _ => throw new ArgumentException($"Invalid value for attribute '{definition.Key}'.", nameof(value))
    };

    private static ResolvedAssignment ResolveEnumeration(AttributeDefinition definition, JsonElement value, bool multiple)
    {
        if (value.ValueKind != JsonValueKind.String || definition.Values.SingleOrDefault(candidate => candidate.Key == value.GetString() && candidate.IsActive) is not { } selected)
            throw new ArgumentException($"Invalid value for attribute '{definition.Key}'.", nameof(value));
        return new(definition, selected);
    }

    private static bool IsUnchanged(Item item, SynchronizeItemRequest request, IReadOnlyCollection<ResolvedAssignment> assignments)
    {
        if (item.Name != request.Name || item.Description != request.Description || item.IsActive != request.IsActive || item.AttributeValues.Count() != assignments.Count)
            return false;

        var existing = item.AttributeValues.ToLookup(value => value.AttributeDefinitionId);
        return assignments.All(assignment => existing[assignment.Definition.Id].Any(value =>
            assignment.Definition.DataType switch
            {
                AttributeDataType.Text => value.TextValue == (string)assignment.Value,
                AttributeDataType.Integer => value.IntegerValue == (long)assignment.Value,
                AttributeDataType.Decimal => value.DecimalValue == (decimal)assignment.Value,
                AttributeDataType.Boolean => value.BooleanValue == (bool)assignment.Value,
                AttributeDataType.Date => value.DateValue == (DateOnly)assignment.Value,
                AttributeDataType.Enumeration or AttributeDataType.MultiEnumeration => value.AttributeValueId == ((AttributeValue)assignment.Value).Id,
                _ => false
            }));
    }

    private static ItemResponse ToResponse(Item item, IEnumerable<ResolvedAssignment> assignments)
        => new(item.Id, item.ExternalId, item.Name, item.Description, item.IsActive, item.UpdatedAt, assignments.Select(assignment => assignment.ToResponse()).ToArray());

    private static ItemResponse ToResponse(Item item, IEnumerable<ItemAttributeValue> assignments)
        => new(item.Id, item.ExternalId, item.Name, item.Description, item.IsActive, item.UpdatedAt, assignments.Select(ToResponse).ToArray());

    private static ItemAttributeResponse ToResponse(ItemAttributeValue assignment)
    {
        object? value = (object?)assignment.AttributeValue?.Key ??
            (assignment.TextValue is not null ? assignment.TextValue :
            assignment.IntegerValue.HasValue ? assignment.IntegerValue.Value :
            assignment.DecimalValue.HasValue ? assignment.DecimalValue.Value :
            assignment.BooleanValue.HasValue ? assignment.BooleanValue.Value :
            assignment.DateValue.HasValue ? assignment.DateValue.Value : null);
        return new ItemAttributeResponse(assignment.AttributeDefinition.Key, assignment.AttributeDefinition.DataType, value);
    }

    private sealed record ResolvedAssignment(AttributeDefinition Definition, object Value)
    {
        public Guid ItemId { get; init; }

        public ItemAttributeValue ToEntity(Guid itemId)
        {
            var entity = new ItemAttributeValue(Definition.TenantId, itemId, Definition.Id);
            switch (Definition.DataType)
            {
                case AttributeDataType.Text: entity.SetText((string)Value); break;
                case AttributeDataType.Integer: entity.IntegerValue = (long)Value; break;
                case AttributeDataType.Decimal: entity.DecimalValue = (decimal)Value; break;
                case AttributeDataType.Boolean: entity.BooleanValue = (bool)Value; break;
                case AttributeDataType.Date: entity.DateValue = (DateOnly)Value; break;
                case AttributeDataType.Enumeration:
                case AttributeDataType.MultiEnumeration: entity.AttributeValueId = ((AttributeValue)Value).Id; break;
            }
            return entity;
        }

        public ItemAttributeResponse ToResponse() => new(Definition.Key, Definition.DataType, Value is AttributeValue attributeValue ? attributeValue.Key : Value);
    }
}
