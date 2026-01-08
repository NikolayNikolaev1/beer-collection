package com.nnikolaev.beercollection.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "companies")
public class Company extends AuditableModel {
    private String name;
    private String description;

    @OneToMany(mappedBy = "company")
    private Set<Beer> beers = new HashSet<>();

    protected Company() { }

    public Company(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }


    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }
}
