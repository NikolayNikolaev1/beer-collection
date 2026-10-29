namespace CollectionPlatform.Common.Extensions;

public static class StringExtension
{
    public static string? NormalizeValue(this string? value)
        => string.IsNullOrWhiteSpace(value) ? null : value.Trim();

    public static string Require(this string? value)
        => string.IsNullOrWhiteSpace(value)
        ? throw new ArgumentException("Value is required", nameof(value))
        : value.Trim();
}
