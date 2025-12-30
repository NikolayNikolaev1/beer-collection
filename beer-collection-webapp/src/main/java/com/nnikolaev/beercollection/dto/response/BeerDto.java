package com.nnikolaev.beercollection.dto.response;

import com.nnikolaev.beercollection.model.enums.*;

import java.util.UUID;

public record BeerDto(
        UUID id,
        String name,
        String description,
        Double price,
        Integer volume,
        BeerColor color,
        BeerTag tag) {
    // TODO: Implement Company and Country object DTOs
}
