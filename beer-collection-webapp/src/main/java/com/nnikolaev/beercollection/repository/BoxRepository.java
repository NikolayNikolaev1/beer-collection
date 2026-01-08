package com.nnikolaev.beercollection.repository;

import com.nnikolaev.beercollection.model.Box;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BoxRepository  extends JpaRepository<Box, UUID> {
    Optional<Box> findByName(String name);
}
