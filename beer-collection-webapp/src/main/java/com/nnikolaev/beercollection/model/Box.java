package com.nnikolaev.beercollection.model;

import com.nnikolaev.beercollection.model.enums.BoxTag;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "boxes")
public class Box extends AuditableModel {
    private String name;
    private String description;
    private Double priceEu;

    @ElementCollection(targetClass = BoxTag.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "box_tags",
            joinColumns = @JoinColumn(name = "box_id")
    )
    @Column(name = "tags", nullable = false, length = 20)
    private Set<BoxTag> tags = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "beer_boxes",
            joinColumns = @JoinColumn(name = "box_id"),
            inverseJoinColumns = @JoinColumn(name = "beer_id")
    )
    private Set<Beer> beers = new HashSet<>();

    @ManyToMany(mappedBy = "boxes")
    private Set<Order> orders = new HashSet<>();

    protected Box() { }

    public Box(
            String name,
            String description,
            Double price,
            Set<BoxTag> tags,
            Set<Beer> beers) {
        this.name = name;
        this.description = description;
        this.priceEu = price;
        this.tags = tags;
        this.beers = beers;
    }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return this.priceEu; }
    public void setPrice(Double price) { this.priceEu = price; }

    public Set<BoxTag> getTags() { return Collections.unmodifiableSet(this.tags); }

}
