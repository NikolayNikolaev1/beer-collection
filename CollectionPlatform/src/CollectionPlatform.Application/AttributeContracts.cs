using CollectionPlatform.Domain.Enums;
using System.ComponentModel.DataAnnotations;

namespace CollectionPlatform.Application;

public sealed record CreateAttributeRequest(
    [property: Required, StringLength(100, MinimumLength = 1)] string Key,
    [property: Required, StringLength(200, MinimumLength = 1)] string Name,
    AttributeDataType DataType,
    bool IsRequired = false,
    bool IsMultiple = false,
    bool IsFilterable = true);

public sealed record UpdateAttributeRequest(
    [property: Required, StringLength(200, MinimumLength = 1)] string Name,
    bool IsRequired,
    bool IsMultiple,
    bool IsFilterable,
    bool IsActive = true);

public sealed record CreateAttributeValueRequest(
    [property: Required, StringLength(100, MinimumLength = 1)] string Key,
    [property: Required, StringLength(200, MinimumLength = 1)] string Name);

public sealed record UpdateAttributeValueRequest(
    [property: Required, StringLength(200, MinimumLength = 1)] string Name,
    bool IsActive = true);

public sealed record AttributeValueResponse(Guid Id, string Key, string Name, bool IsActive);
public sealed record AttributeResponse(Guid Id, string Key, string Name, AttributeDataType DataType, bool IsRequired, bool IsMultiple, bool IsFilterable, bool IsActive, IReadOnlyList<AttributeValueResponse> Values);

public interface IAttributeService
{
    Task<AttributeResponse> CreateAsync(CreateAttributeRequest request, CancellationToken cancellationToken);
    Task<IReadOnlyList<AttributeResponse>> GetAsync(CancellationToken cancellationToken);
    Task<AttributeResponse?> GetAsync(Guid id, CancellationToken cancellationToken);
    Task<AttributeResponse?> UpdateAsync(Guid id, UpdateAttributeRequest request, CancellationToken cancellationToken);
    Task<bool> DeactivateAsync(Guid id, CancellationToken cancellationToken);
    Task<AttributeValueResponse> CreateValueAsync(Guid attributeId, CreateAttributeValueRequest request, CancellationToken cancellationToken);
    Task<IReadOnlyList<AttributeValueResponse>?> GetValuesAsync(Guid attributeId, CancellationToken cancellationToken);
    Task<AttributeValueResponse?> UpdateValueAsync(Guid attributeId, Guid valueId, UpdateAttributeValueRequest request, CancellationToken cancellationToken);
    Task<bool> DeactivateValueAsync(Guid attributeId, Guid valueId, CancellationToken cancellationToken);
}
