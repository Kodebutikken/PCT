package com.kodebutikken.pct.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceForm {

    @NotBlank(message = "Navn må ikke være tomt")
    private String name;

    @NotBlank(message = "Kompetencer må ikke være tomme")
    private String skills;

    @NotNull(message = "Daglige arbejdstimer må ikke være tomt")
    @Max(value = 24, message = "Daglige arbejdstimer må ikke overstige 24")
    private int dailyWorkingHours;

    @NotNull(message = "Timeløn må ikke være tomt")
    private double hourlyWage;
}
