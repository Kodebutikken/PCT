package com.kodebutikken.pct.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data

public class Subproject {
    private int id;
    private String title;
    private String description;
    private LocalDate deadline;
    private int projectId;
    private LocalDateTime createdAt;

    public Subproject(int id, String title, String description, LocalDate deadline, int projectId, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.projectId = projectId;
        this.createdAt = createdAt;
    }

    public Subproject() {
    }

}



