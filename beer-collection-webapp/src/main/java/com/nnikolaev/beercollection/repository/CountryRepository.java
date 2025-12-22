package com.nnikolaev.beercollection.repository;

import com.nnikolaev.beercollection.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface CountryRepository extends JpaRepository<Country, UUID> {
    Optional<Country> findByNameOrCode(String name, String code); // TODO: Check if this works with OR
}
