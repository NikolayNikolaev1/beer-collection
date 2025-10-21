package com.nnikolaev.beercollection.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "beers")
public class Beer {
    @Id
    private Integer id;
    private String name;
    private String description;
    private Double priceEu;
    private Double alcoholPct;
    private Integer volumeMl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ManyToMany(mappedBy = "beers")
    private Set<Box> boxes = new HashSet<>();
}
