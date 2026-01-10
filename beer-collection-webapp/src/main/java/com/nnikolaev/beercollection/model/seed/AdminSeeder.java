package com.nnikolaev.beercollection.model.seed;

import com.nnikolaev.beercollection.model.enums.UserRole;
import com.nnikolaev.beercollection.repository.UserRepository;
import com.nnikolaev.beercollection.service.UserService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.nnikolaev.beercollection.common.Constant.AppConfigValue;

@Component
@Order(1)
public class AdminSeeder implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Value(AppConfigValue.ADMIN_EMAIL)
    private String adminEmail;
    @Value(AppConfigValue.ADMIN_PASSWORD)
    private String adminPassword;

    @Override
    public void run(String... args) {
        boolean adminExists = this.userRepository.existsByRole(UserRole.ADMIN);

        if (adminExists) return;

        this.userService.create(adminEmail, adminPassword, UserRole.ADMIN);
    }
}