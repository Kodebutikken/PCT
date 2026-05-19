package com.kodebutikken.pct.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceForm {

    @NotBlank(message = "Navn må ikke være tomt")
    private String name;

    @NotBlank(message = "Kompetencer må ikke være tomme")
    private String compentencies;

    @NotBlank(message = "Daglige arbejdstimer må ikke være tomt")
    private int dailyWorkingHours;

    @NotBlank(message = "Timeløn må ikke være tomt")
    private double hourlyWage;
}
