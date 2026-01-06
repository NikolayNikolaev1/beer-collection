package com.nnikolaev.beercollection.dto.request;

import com.nnikolaev.beercollection.model.enums.*;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record BeerUpsertDto(
        @NotBlank
        @Size(min = 2, max = 128)
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

/*
*
* public record BeerDto(
        UUID id,
        String name,
        String description,
        Double price,
        Integer volume,
        BeerColor color,
        BeerTag tag) {
    // TODO: Implement Company and Country object DTOs
}*/