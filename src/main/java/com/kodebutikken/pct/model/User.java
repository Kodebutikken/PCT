package com.kodebutikken.pct.model;

import lombok.Data;

@Data
public class User {
    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private Role role;

    public User (int id, String name, String email, String passwordHash, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }
}
