package com.nnikolaev.beercollection.dto.response.error;

import java.util.*;

public record ValidationErrorResponse(
        int status,
        String message,
        Set<Violation> errors,
        String path) {
    @Override
    public Set<Violation> errors() {
        return Collections.unmodifiableSet(errors);
    }
}
