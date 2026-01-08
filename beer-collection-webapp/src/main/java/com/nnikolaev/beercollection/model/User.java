package com.nnikolaev.beercollection.model;

import com.nnikolaev.beercollection.model.enums.UserRole;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "users")
public class User extends AuditableModel {
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user")
    private Set<Order> orders = new HashSet<>();

    protected User() { }

    public User(String email, String password, UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return this.role; }
    public void setRole(UserRole role) { this.role = role; }

    public Set<Order> getOrders() { return Collections.unmodifiableSet(this.orders); }
}
