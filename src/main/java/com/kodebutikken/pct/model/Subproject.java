package com.kodebutikken.pct.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data

public class Subproject {
    private int id;
    private String titel;
    private String beskrivelse;
    private LocalDate deadline;
    private int projectId;
    private LocalDateTime createdAt;

    public Subproject(int id, String titel, String beskrivelse, LocalDate deadline, int projectId, LocalDateTime createdAt) {
        this.id = id;
        this.titel = titel;
        this.beskrivelse = beskrivelse;
        this.deadline = deadline;
        this.projectId = projectId;
        this.createdAt = createdAt;
    }

    public Subproject() {
    }

}



