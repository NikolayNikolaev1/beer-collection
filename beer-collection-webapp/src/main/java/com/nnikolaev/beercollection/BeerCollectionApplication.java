package com.nnikolaev.beercollection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.sql.SQLException;

@SpringBootApplication
@EnableJpaAuditing
public class BeerCollectionApplication {
    public static void main(String[] args) throws SQLException {
        SpringApplication.run(BeerCollectionApplication.class, args);
    }
}
