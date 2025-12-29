package com.nnikolaev.beercollection.dto.response;

import com.nnikolaev.beercollection.model.enums.UserRole;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
        UUID id,
        String email,
        UserRole role,
        boolean isActive,
        Instant modifiedAt) {
}
