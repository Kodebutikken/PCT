package com.kodebutikken.pct.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceForm {

    @NotBlank(message = "Navn må ikke være tomt")
    @Size(max = 255, message = "Navn må ikke overstige 255 tegn")
    private String name;

    @NotBlank(message = "Kompetencer må ikke være tomme")
    @Size(max = 50, message = "Kompetencer må ikke overstige 50 tegn")
    private String skills;

    @NotNull(message = "Daglige arbejdstimer må ikke være tomt")
    @Max(value = 24, message = "Daglige arbejdstimer må ikke overstige 24")
    private int dailyWorkingHours;

    @NotNull(message = "Timeløn må ikke være tomt")
    private double hourlyWage;
}
