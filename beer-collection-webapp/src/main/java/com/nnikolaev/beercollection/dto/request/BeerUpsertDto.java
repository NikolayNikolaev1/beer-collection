package com.nnikolaev.beercollection.dto.request;

import com.nnikolaev.beercollection.model.enums.*;
import jakarta.validation.constraints.*;

import java.util.UUID;

import static com.nnikolaev.beercollection.common.Constant.Validation;

public record BeerUpsertDto(
        @NotBlank
        @Size(min = Validation.TEXT_MIN_LENGTH, max = Validation.TEXT_MAX_LENGTH)
        String name,
        @Size(max = 256)
        String description,
        @NotNull
        @Min(0) @Max(128)
        Double price,
        @NotNull
        @Min(330) @Max(10000)
        Integer volume,
        @NotNull
        @Min(0) @Max(30)
        Double alcohol,
        BeerColor color,
        BeerTag tag,
        @NotNull
        UUID companyId,
        @NotNull
        UUID countryId) {
}