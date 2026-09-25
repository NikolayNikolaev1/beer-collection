namespace CollectionPlatform.Application;

using Domain.Enums;
using System.ComponentModel.DataAnnotations;

public sealed record SynchronizeUserRequest(
    [property: Required, StringLength(200, MinimumLength = 1)] string ExternalId,
    [property: StringLength(300)] string? DisplayName,
    bool IsActive = true);

public sealed record UserResponse(Guid Id, string ExternalId, string? DisplayName, bool IsActive, DateTimeOffset UpdatedAt);

public interface IUserSynchronizationService
{
    Task<(UserResponse User, SynchronizationStatus Status)> SynchronizeAsync(SynchronizeUserRequest request, CancellationToken cancellationToken);
    Task<IReadOnlyList<UserResponse>> GetAsync(int page, int pageSize, CancellationToken cancellationToken);
}
