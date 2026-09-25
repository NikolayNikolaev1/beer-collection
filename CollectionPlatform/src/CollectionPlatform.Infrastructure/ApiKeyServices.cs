namespace CollectionPlatform.Infrastructure;

using Application;
using Microsoft.EntityFrameworkCore;
using System.Security.Cryptography;
using System.Text;

public sealed class ApiKeyValidator(CollectionPlatformDbContext dbContext) : IApiKeyValidator
{
    public async Task<ApiKeyValidationResult?> ValidateAsync(string credential, CancellationToken cancellationToken)
    {
        if (string.IsNullOrWhiteSpace(credential)) return null;

        var separator = credential.IndexOf('_');
        if (separator <= 0 || separator == credential.Length - 1) return null;

        var prefix = credential[..separator];
        var hash = Hash(credential);
        var key = await dbContext.ApiKeys
            .IgnoreQueryFilters()
            .AsNoTracking()
            .SingleOrDefaultAsync(candidate => candidate.KeyPrefix == prefix && candidate.IsActive, cancellationToken);

        if (key is null || !CryptographicOperations.FixedTimeEquals(Encoding.UTF8.GetBytes(key.KeyHash), Encoding.UTF8.GetBytes(hash))) return null;
        return new ApiKeyValidationResult(key.Id, key.TenantId);
    }

    public static string Hash(string credential) => Convert.ToHexString(SHA256.HashData(Encoding.UTF8.GetBytes(credential)));
}

public static class ApiKeyGenerator
{
    public static (string Credential, string Prefix, string Hash) Create() 
    {
        var prefix = "cp" + Convert.ToHexString(RandomNumberGenerator.GetBytes(4)).ToLowerInvariant();
        var secret = Convert.ToBase64String(RandomNumberGenerator.GetBytes(32)).Replace('+', '-').Replace('/', '_').TrimEnd('=');
        var credential = $"{prefix}_{secret}";
        return (credential, prefix, ApiKeyValidator.Hash(credential));
    }
}
