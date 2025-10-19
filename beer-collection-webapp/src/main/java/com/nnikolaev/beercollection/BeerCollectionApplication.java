package com.nnikolaev.beercollection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.DriverManager;
import java.sql.SQLException;


@SpringBootApplication
public class BeerCollectionApplication {
    public static void main(String[] args) throws SQLException {
//        DriverManager.getConnection("jdbc:sqlserver://localhost:1344;databaseName=BeerCollectionDB;encrypt=true;trustServerCertificate=true","springappuser","Admin123");
        SpringApplication.run(BeerCollectionApplication.class, args);
    }
}
