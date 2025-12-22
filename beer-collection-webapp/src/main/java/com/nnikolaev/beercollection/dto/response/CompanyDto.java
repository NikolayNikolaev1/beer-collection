package com.nnikolaev.beercollection.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CompanyDto(
        UUID Id,
        String name,
        String description,
        Instant modified_at) {
}
