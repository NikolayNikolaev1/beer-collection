package com.nnikolaev.beercollection.exception;

public class EmailAlreadyUsedException extends RuntimeException {
    private static final String EMAIL_EXISTS_ERROR_MESSAGE = "User with email already exists in database.";

    public EmailAlreadyUsedException() {
        this(EMAIL_EXISTS_ERROR_MESSAGE);
    }

    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
