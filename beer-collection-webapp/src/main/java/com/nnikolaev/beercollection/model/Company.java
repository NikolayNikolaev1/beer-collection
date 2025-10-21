package com.nnikolaev.beercollection.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "companies")
public class Company {
    @Id
    private Integer id;
    private String name;
    private String description;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Beer> beers = new HashSet<>();
}
