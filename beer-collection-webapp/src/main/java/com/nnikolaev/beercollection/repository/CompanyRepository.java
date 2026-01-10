package com.nnikolaev.beercollection.repository;

import com.nnikolaev.beercollection.model.Company;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface CompanyRepository extends JpaRepository<Company, UUID>, JpaSpecificationExecutor<Company> {
    Optional<Company> findByName(String name);
}
