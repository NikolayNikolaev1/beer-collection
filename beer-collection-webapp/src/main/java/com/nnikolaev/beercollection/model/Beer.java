package com.nnikolaev.beercollection.model;

import com.nnikolaev.beercollection.model.enums.BeerColor;
import com.nnikolaev.beercollection.model.enums.BeerTag;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "beers")
public class Beer extends AuditableModel {
    private String name;
    private String description;
    private Double priceEu;
    private Double alcoholPct;
    private Integer volumeMl;
    @Enumerated(EnumType.STRING)
    private BeerColor color;
    @Enumerated(EnumType.STRING)
    private BeerTag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ManyToMany(mappedBy = "beers")
    private Set<Box> boxes = new HashSet<>();

    protected Beer() { }

    public Beer(
            String name,
            String description,
            Double price,
            Double alcohol,
            Integer volume,
            BeerColor color,
            BeerTag tag) {
        this.name = name;
        this.description = description;
        this.priceEu = price;
        this.alcoholPct = alcohol;
        this.volumeMl = volume;
        this.color = color;
        this.tag = tag;
    }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return this.priceEu; }
    public void setPrice(Double price) { this.priceEu = price; }

    public Double getAlcohol() { return this.alcoholPct; }

    public Integer getVolume() { return this.volumeMl; }

    public BeerColor getColor() { return this.color; }
    public void setColor(BeerColor color) { this.color = color; }

    public BeerTag getTag() { return this.tag; }
    public void setTag(BeerTag tag) { this.tag = tag; }

    public Set<Box> getBoxes() { return Collections.unmodifiableSet(this.boxes); }
}
