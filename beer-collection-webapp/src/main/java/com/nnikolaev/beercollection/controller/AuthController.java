package com.nnikolaev.beercollection.controller;

import com.nnikolaev.beercollection.dto.request.AuthRequest;
import com.nnikolaev.beercollection.dto.response.AuthResponse;
import com.nnikolaev.beercollection.exception.user.UserNotFoundException;
import com.nnikolaev.beercollection.security.ResponseHandler;
import com.nnikolaev.beercollection.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.nnikolaev.beercollection.common.Constant.Route;

@RestController
@RequestMapping(Route.AUTH_PREFIX)
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping(Route.REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest req) {
        return ResponseHandler.success(authService.register(req));
    }

    @PostMapping(Route.LOGIN)
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req)
            throws UserNotFoundException {
        return ResponseHandler.success(authService.login(req));
    }

    @PostMapping(Route.LOGOUT)
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        return ResponseHandler.success(authService.logout(authHeader));
    }
}
