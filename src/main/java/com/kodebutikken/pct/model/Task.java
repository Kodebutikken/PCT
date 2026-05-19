package com.kodebutikken.pct.model;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Data
public class Task {
    private int id;
    private String title;
    private String description;
    private double estimatedHours;
    private LocalDate deadline;
    private int subprojectId;
    private int resourceTypeId;
    private int resourceId;
    private LocalDate createdAt;

    public Task(int id, String title, String description, double estimatedHours, LocalDate deadline, int subprojectId, int resourceTypeId, int resourceId, LocalDate createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.estimatedHours = estimatedHours;
        this.deadline = deadline;
        this.subprojectId = subprojectId;
        this.resourceTypeId = resourceTypeId;
        this.resourceId = resourceId;
        this.createdAt = createdAt;
    }
}
