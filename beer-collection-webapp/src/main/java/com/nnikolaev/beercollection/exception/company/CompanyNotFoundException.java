package com.nnikolaev.beercollection.exception.company;

public class CompanyNotFoundException extends RuntimeException {
    private static final String ERROR_MESSAGE = "Company with provided id does not exists.";

    public CompanyNotFoundException() { this(ERROR_MESSAGE); }

    public CompanyNotFoundException(String message) {
        super(message);
    }
}
