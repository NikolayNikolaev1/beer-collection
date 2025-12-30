package com.nnikolaev.beercollection.exception;

public class OperationConflictException extends RuntimeException {
    public OperationConflictException(String message) {
        super(message);
    }
}
