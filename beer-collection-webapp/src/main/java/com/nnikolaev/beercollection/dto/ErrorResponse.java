package com.nnikolaev.beercollection.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
    private Integer status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private String path;

    public ErrorResponse(
            Integer status,
            String error,
            String message,
            LocalDateTime timestamp,
            String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
    }
}
