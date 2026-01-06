package com.nnikolaev.beercollection.dto.response;

import java.util.UUID;

public record CountryDto(UUID id, String name, String code) {
}
