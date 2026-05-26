package com.kodebutikken.pct.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Project {
    private int id;
    private String title;
    private String description;
    private LocalDate deadline;
    private int createdBy;
    private LocalDateTime createdAt;

    public Project () {}

    public Project (int id, String title, String description, LocalDate deadline, int createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
}
