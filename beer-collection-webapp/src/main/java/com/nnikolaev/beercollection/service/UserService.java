package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.model.enums.UserRole;

public interface UserService {
    void create(String email, String password, UserRole role);
}
