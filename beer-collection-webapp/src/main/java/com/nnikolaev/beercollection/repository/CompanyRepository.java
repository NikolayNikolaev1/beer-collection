package com.nnikolaev.beercollection.repository;

import com.nnikolaev.beercollection.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    @Override
    List<Company> findAll();

    @Override
    Optional<Company> findById(UUID id);

    Optional<Company> findByName(String name);
}
