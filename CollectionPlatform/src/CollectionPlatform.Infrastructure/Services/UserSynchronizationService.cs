namespace CollectionPlatform.Infrastructure.Services;

using Application;
using Common.Extensions;
using Domain.Entities;
using Domain.Enums;
using Microsoft.EntityFrameworkCore;

public sealed class UserSynchronizationService(
    CollectionPlatformDbContext dbContext,
    ITenantContext tenantContext) : IUserSynchronizationService
{
    public async Task<(UserResponse User, SynchronizationStatus Status)> SynchronizeAsync(SynchronizeUserRequest request, CancellationToken cancellationToken)
    {
        var tenantId = RequireTenant();
        var externalId = request.ExternalId.Require();
        var user = await dbContext.Users.SingleOrDefaultAsync(candidate => candidate.ExternalId == externalId, cancellationToken);
        var status = SynchronizationStatus.Created;

        if (user is null)
        {
            user = new User(tenantId, externalId, request.DisplayName);
            await dbContext.Users.AddAsync(user);
        }
        else
        {
            var previousName = user.DisplayName;
            var previousActive = user.IsActive;
            user.Synchronize(request.DisplayName, request.IsActive);
            status = previousName == user.DisplayName && previousActive == user.IsActive
                ? SynchronizationStatus.Unchanged
                : SynchronizationStatus.Updated;

            dbContext.Users.Update(user);
        }

        await dbContext.SaveChangesAsync(cancellationToken);
        return (new UserResponse(user.Id, user.ExternalId, user.DisplayName, user.IsActive, user.UpdatedAt), status);
    }

    public async Task<BulkSynchronizationResponse<BulkUserResult>> SynchronizeBulkAsync(
        IReadOnlyList<SynchronizeUserRequest> requests,
        CancellationToken cancellationToken)
    {
        var results = new List<BulkUserResult>();
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
                results.Add(new BulkUserResult(result.User, status));
                switch (result.Status)
                {
                    case SynchronizationStatus.Created: created++; break;
                    case SynchronizationStatus.Updated: updated++; break;
                    case SynchronizationStatus.Unchanged: unchanged++; break;
                }
            }
            catch (Exception exception) when (exception is ArgumentException or InvalidOperationException or DbUpdateException)
            {
                errors.Add(new BulkRecordError(request.ExternalId, "USER_SYNC_FAILED", exception.Message));
            }
        }

        return new BulkSynchronizationResponse<BulkUserResult>(
            requests.Count,
            created,
            updated,
            unchanged,
            errors.Count,
            results,
            errors);
    }

    public async Task<IReadOnlyList<UserResponse>> GetAsync(int page, int pageSize, CancellationToken cancellationToken)
    => await dbContext.Users
        .AsNoTracking()
        .OrderBy(user => user.Id)
        .Skip((page - 1) * pageSize)
        .Take(pageSize)
        .Select(user => new UserResponse(user.Id, user.ExternalId, user.DisplayName, user.IsActive, user.UpdatedAt))
        .ToListAsync(cancellationToken);

    private Guid RequireTenant() => tenantContext.TenantId
        ?? throw new InvalidOperationException("An authenticated tenant is required.");
}
