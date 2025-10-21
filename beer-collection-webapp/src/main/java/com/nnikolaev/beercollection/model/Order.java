package com.nnikolaev.beercollection.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    private Integer id;
    private User user;

    @ManyToMany
    @JoinTable(
            name = "box_orders",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "box_id")
    )
    private Set<Box> boxes = new HashSet<>();
}
