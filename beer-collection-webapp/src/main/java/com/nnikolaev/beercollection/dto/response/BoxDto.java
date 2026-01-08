package com.nnikolaev.beercollection.dto.response;

import java.util.UUID;

public record BoxDto(
        UUID id,
        String name,
        String description,
        Double price,
        boolean isDeleted) {
}
