package com.nnikolaev.beercollection.security;

import org.springframework.http.ResponseEntity;

public class ResponseHandler {
    public static ResponseEntity<?> success(Void e) {
        return ResponseEntity.noContent().build();
    }

    public static <T> ResponseEntity<T> success(T data) {
        return ResponseEntity.ok(data);
    }
}
