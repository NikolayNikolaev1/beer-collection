package com.nnikolaev.beercollection.exception;

public class CompanyExistsException extends RuntimeException {
    private static final String ERROR_MESSAGE = "Company with provided name already exists.";

    public CompanyExistsException() { this(ERROR_MESSAGE); }

    public CompanyExistsException(String message) {
        super(message);
    }
}
