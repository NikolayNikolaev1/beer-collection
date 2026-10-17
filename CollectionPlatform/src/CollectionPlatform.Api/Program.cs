using CollectionPlatform.Api;
using CollectionPlatform.Application;
using CollectionPlatform.Infrastructure;
using CollectionPlatform.Infrastructure.Services;
using Microsoft.AspNetCore.Authentication;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddScoped<TenantContext>();
builder.Services.AddScoped<ITenantContext>(services => services.GetRequiredService<TenantContext>());
builder.Services.AddDbContext<CollectionPlatformDbContext>(options =>
	options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));
builder.Services.AddScoped<IApiKeyValidator, ApiKeyValidator>();
builder.Services.AddScoped<IUserSynchronizationService, UserSynchronizationService>();
builder.Services.AddScoped<IAttributeService, AttributeService>();
builder.Services.AddScoped<IItemSynchronizationService, ItemSynchronizationService>();
builder.Services.AddScoped<ICollectionService, CollectionService>();
builder.Services
	.AddAuthentication(ApiKeyAuthenticationHandler.SchemeName)
	.AddScheme<AuthenticationSchemeOptions, ApiKeyAuthenticationHandler>(ApiKeyAuthenticationHandler.SchemeName, _ => { });
builder.Services.AddAuthorization();

var app = builder.Build();

app.UseExceptionHandler(errorApp => errorApp.Run(async context =>
{
	var exception = context.Features.Get<Microsoft.AspNetCore.Diagnostics.IExceptionHandlerFeature>()?.Error;
	var status = exception switch
	{
		ArgumentException => StatusCodes.Status400BadRequest,
		KeyNotFoundException => StatusCodes.Status404NotFound,
		_ => StatusCodes.Status500InternalServerError
	};
	context.Response.StatusCode = status;
	context.Response.ContentType = "application/problem+json";
	await context.Response.WriteAsJsonAsync(new
	{
		type = "https://httpstatuses.com/" + status,
		title = status == 500 ? "An unexpected error occurred." : "The request could not be completed.",
		status,
		detail = status == 500 ? "The server could not process the request." : exception?.Message,
		traceId = context.TraceIdentifier
	});
}));
app.UseHttpsRedirection();
app.UseSwagger();
app.UseSwaggerUI();
app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

app.Run();
