package com.nnikolaev.beercollection.dto.request;

import jakarta.validation.constraints.*;

public record CompanyUpsertDto(
        @NotBlank @Size(min = 3, max = 127) String name,
        String description) {
}
