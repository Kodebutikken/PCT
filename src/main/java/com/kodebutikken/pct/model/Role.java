package com.kodebutikken.pct.model;

public enum Role {
    PROJECT_MANAGER("Projektleder"),
    DEVELOPER("Udvikler"),
    ADMINISTRATOR("Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }
}