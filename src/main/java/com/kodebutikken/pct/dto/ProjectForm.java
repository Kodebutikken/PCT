package com.kodebutikken.pct.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class ProjectForm {

    @Getter
    @Setter
    @NotBlank(message = "Project title is required")
    private String title;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate deadline;

}
