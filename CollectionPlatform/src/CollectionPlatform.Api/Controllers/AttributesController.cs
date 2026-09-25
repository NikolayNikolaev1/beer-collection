namespace CollectionPlatform.Api.Controllers;

using Application;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;


[ApiController]
[Authorize]
[Route("api/v1/attributes")]
public sealed class AttributesController(IAttributeService attributes) : ControllerBase
{
    [HttpPost]
    public async Task<ActionResult<AttributeResponse>> Create(CreateAttributeRequest request, CancellationToken cancellationToken)
        => Ok(await attributes.CreateAsync(request, cancellationToken));

    [HttpGet]
    public async Task<ActionResult<IReadOnlyList<AttributeResponse>>> Get(CancellationToken cancellationToken)
        => Ok(await attributes.GetAsync(cancellationToken));

    [HttpGet("{id:guid}")]
    public async Task<ActionResult<AttributeResponse>> Get(Guid id, CancellationToken cancellationToken)
        => await attributes.GetAsync(id, cancellationToken) is { } result ? Ok(result) : NotFound();

    [HttpPut("{id:guid}")]
    public async Task<ActionResult<AttributeResponse>> Update(Guid id, UpdateAttributeRequest request, CancellationToken cancellationToken)
        => await attributes.UpdateAsync(id, request, cancellationToken) is { } result ? Ok(result) : NotFound();

    [HttpDelete("{id:guid}")]
    public async Task<IActionResult> Delete(Guid id, CancellationToken cancellationToken)
        => await attributes.DeactivateAsync(id, cancellationToken) ? NoContent() : NotFound();

    [HttpPost("{attributeId:guid}/values")]
    public async Task<ActionResult<AttributeValueResponse>> CreateValue(Guid attributeId, CreateAttributeValueRequest request, CancellationToken cancellationToken)
        => Ok(await attributes.CreateValueAsync(attributeId, request, cancellationToken));

    [HttpGet("{attributeId:guid}/values")]
    public async Task<ActionResult<IReadOnlyList<AttributeValueResponse>>> GetValues(Guid attributeId, CancellationToken cancellationToken)
        => await attributes.GetValuesAsync(attributeId, cancellationToken) is { } result ? Ok(result) : NotFound();

    [HttpPut("{attributeId:guid}/values/{valueId:guid}")]
    public async Task<ActionResult<AttributeValueResponse>> UpdateValue(Guid attributeId, Guid valueId, UpdateAttributeValueRequest request, CancellationToken cancellationToken)
        => await attributes.UpdateValueAsync(attributeId, valueId, request, cancellationToken) is { } result ? Ok(result) : NotFound();

    [HttpDelete("{attributeId:guid}/values/{valueId:guid}")]
    public async Task<IActionResult> DeleteValue(Guid attributeId, Guid valueId, CancellationToken cancellationToken)
        => await attributes.DeactivateValueAsync(attributeId, valueId, cancellationToken) ? NoContent() : NotFound();
}
