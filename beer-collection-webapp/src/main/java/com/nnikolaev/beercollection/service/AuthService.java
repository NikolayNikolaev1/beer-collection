package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.dto.request.AuthRequest;
import com.nnikolaev.beercollection.dto.response.AuthResponse;

public interface AuthService {
    Void register(AuthRequest requestDto);

    AuthResponse login(AuthRequest requestDto);

    Void logout(String authHeader);
}
