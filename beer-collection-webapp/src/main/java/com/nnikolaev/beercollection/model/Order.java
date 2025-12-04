package com.nnikolaev.beercollection.model;

import com.nnikolaev.beercollection.model.enums.OrderStatus;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "orders")
public class Order extends AuditableModel {
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "box_orders",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "box_id")
    )
    private Set<Box> boxes = new HashSet<>();

    protected Order() { }

    public OrderStatus getStatus() { return this.status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public Set<Box> getBoxes() { return Collections.unmodifiableSet(this.boxes); }
}
