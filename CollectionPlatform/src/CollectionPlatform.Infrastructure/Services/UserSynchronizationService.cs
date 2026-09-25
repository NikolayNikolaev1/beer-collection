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
