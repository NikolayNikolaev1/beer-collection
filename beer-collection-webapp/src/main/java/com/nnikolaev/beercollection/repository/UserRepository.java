package com.nnikolaev.beercollection.repository;

import com.nnikolaev.beercollection.model.User;
import com.nnikolaev.beercollection.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByRole(UserRole role);
}
