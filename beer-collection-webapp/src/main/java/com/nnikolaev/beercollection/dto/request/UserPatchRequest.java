package com.nnikolaev.beercollection.dto.request;

import com.nnikolaev.beercollection.model.enums.UserRole;

public record UserPatchRequest(String email, UserRole role, Boolean isDeleted) {
}
