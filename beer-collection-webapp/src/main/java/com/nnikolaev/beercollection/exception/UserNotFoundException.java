package com.nnikolaev.beercollection.exception;

public class UserNotFoundException extends RuntimeException {
    private static final String ERROR_MESSAGE = "User with provided email or password does not exist.";

    public UserNotFoundException() {
        this(ERROR_MESSAGE);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
