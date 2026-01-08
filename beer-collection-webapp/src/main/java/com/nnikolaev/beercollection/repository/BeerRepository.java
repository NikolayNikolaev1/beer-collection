package com.nnikolaev.beercollection.repository;

import com.nnikolaev.beercollection.model.Beer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerRepository extends JpaRepository<Beer, UUID> {
    boolean existsByCompanyId(UUID companyId);
}
