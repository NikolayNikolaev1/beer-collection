package com.nnikolaev.beercollection.exception;

public class UserExistsException extends RuntimeException {
    private static final String ERROR_MESSAGE = "User with email already exists in database.";

    public UserExistsException() {
        this(ERROR_MESSAGE);
    }

    public UserExistsException(String message) {
        super(message);
    }
}
