package com.nnikolaev.beercollection.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "boxes")
public class Box {
    @Id
    private Integer id;
    private String name;
    private String description;
    private Double priceEu;

    @ManyToMany
    @JoinTable(
            name = "beer_boxes",
            joinColumns = @JoinColumn(name = "box_id"),
            inverseJoinColumns = @JoinColumn(name = "beer_id")
    )
    private Set<Beer> beers = new HashSet<>();

    @ManyToMany(mappedBy = "boxes")
    private Set<Order> orders = new HashSet<>();
}
