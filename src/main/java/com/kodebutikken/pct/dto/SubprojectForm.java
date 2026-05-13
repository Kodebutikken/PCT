package com.kodebutikken.pct.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class SubprojectForm {

    @Getter
    @Setter

    @NotBlank(message = "Subproject title is required")
    private String title;

    @Getter
    @Setter

    private String description;

    @Getter
    @Setter

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be a future date")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate deadline;
}