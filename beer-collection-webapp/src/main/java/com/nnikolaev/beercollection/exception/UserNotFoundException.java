package com.nnikolaev.beercollection.exception;

public class UserNotFoundException extends RuntimeException {
    private static final String USER_NOT_FOUND_ERROR = "User with provided email or password does not exist.";

    public UserNotFoundException() {
        this(USER_NOT_FOUND_ERROR);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
