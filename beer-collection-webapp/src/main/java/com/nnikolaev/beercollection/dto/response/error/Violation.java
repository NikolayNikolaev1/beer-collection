package com.nnikolaev.beercollection.dto.response.error;

public record Violation(
        String field,
        String message) {
}
