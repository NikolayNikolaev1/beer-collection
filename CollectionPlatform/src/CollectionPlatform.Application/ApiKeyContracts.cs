namespace CollectionPlatform.Application;

public sealed record ApiKeyValidationResult(Guid ApiKeyId, Guid TenantId);

public interface IApiKeyValidator
{
    Task<ApiKeyValidationResult?> ValidateAsync(string credential, CancellationToken cancellationToken);
}
