package com.nnikolaev.beercollection.exception;

public class CountryExistsException extends RuntimeException {
    private static final String ERROR_MESSAGE = "Country that is being created already exists.";

    public CountryExistsException() { this(ERROR_MESSAGE); }

    public CountryExistsException(String message) {
        super(message);
    }
}
