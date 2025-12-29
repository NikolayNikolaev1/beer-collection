package com.nnikolaev.beercollection.service;

public interface CountryService {
    boolean existsAny();

    void create(String name, String code);
}
