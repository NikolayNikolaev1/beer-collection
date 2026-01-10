package com.nnikolaev.beercollection.repository;

import com.nnikolaev.beercollection.model.Beer;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface BeerRepository extends JpaRepository<Beer, UUID>, JpaSpecificationExecutor<Beer> {
    boolean existsByCompanyId(UUID companyId);

    List<Beer> findAllByIdInAndDeletedAtIsNull(Collection<UUID> ids);
}
