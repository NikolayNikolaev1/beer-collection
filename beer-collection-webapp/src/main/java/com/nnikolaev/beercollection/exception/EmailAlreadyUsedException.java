package com.nnikolaev.beercollection.exception;

public class EmailAlreadyUsedException extends RuntimeException {
    private static final String ERROR_MESSAGE = "User with email already exists in database.";

    public EmailAlreadyUsedException() {
        this(ERROR_MESSAGE);
    }

    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
