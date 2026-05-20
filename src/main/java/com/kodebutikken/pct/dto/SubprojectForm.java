package com.kodebutikken.pct.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Getter
@Setter
public class SubprojectForm {
    @NotBlank(message = "Subprojekt titel må ikke være tom!")
    private String title;

    private String description;

    @NotNull(message = "En deadline er påkrævet!")
    @Future(message = "Deadlinen skal være en fremtidig dato")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deadline;
}