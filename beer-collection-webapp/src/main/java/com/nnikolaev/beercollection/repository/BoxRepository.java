package com.nnikolaev.beercollection.repository;

import com.nnikolaev.beercollection.model.Box;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface BoxRepository  extends JpaRepository<Box, UUID>, JpaSpecificationExecutor<Box> {
    Optional<Box> findByName(String name);
}
