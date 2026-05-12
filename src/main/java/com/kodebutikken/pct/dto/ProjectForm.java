package com.kodebutikken.pct.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
@Getter
@Setter
public class ProjectForm {
    @NotBlank(message = "Projekttittel må ikke være tom")
    private String title;

    private String description;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate deadline;

}
