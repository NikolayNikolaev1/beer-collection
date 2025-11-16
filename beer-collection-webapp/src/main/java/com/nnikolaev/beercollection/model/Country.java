package com.nnikolaev.beercollection.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "countries")
public class Country extends AuditableModel {
    private String name;
    private String code;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Beer> beers = new HashSet<>();

    protected Country() { }

    public Country(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() { return this.name; }

    public String getCode() { return this.code; }
}
