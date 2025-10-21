package com.nnikolaev.beercollection.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    private Integer Id;
    private String email;
    private String password;

}
