package com.nnikolaev.beercollection.dto.response;

import com.nnikolaev.beercollection.model.enums.*;

import java.util.UUID;

public record BeerDto(
        UUID id,
        String name,
        String description,
        Double price,
        Integer volume,
        Double alcohol,
        BeerColor color,
        BeerTag tag,
        boolean isLegacy,
        CompanyDto company
        // TODO: Add country mapper
) {
}
