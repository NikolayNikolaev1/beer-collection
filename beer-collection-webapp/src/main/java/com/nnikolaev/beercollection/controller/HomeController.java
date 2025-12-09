package com.nnikolaev.beercollection.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public ResponseEntity<?> home() {
        return ResponseEntity.ok("");
    }


    @GetMapping("/api/admin")
    public ResponseEntity<?> admin() {
        return ResponseEntity.ok("");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("");
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/user")
    public ResponseEntity<?> user() {
        return ResponseEntity.ok("");
    }
}
