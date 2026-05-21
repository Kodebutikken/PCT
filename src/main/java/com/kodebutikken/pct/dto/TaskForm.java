package com.kodebutikken.pct.dto;


import com.kodebutikken.pct.model.Resource;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class TaskForm {

    @NotBlank(message = "Titlen må ikke være tom")
    private String title;

    @NotBlank(message = "Opgaver skal have en beskrivelse")
    private String description;

    @DecimalMin(value = "0.5", message = "Estimeret tid skal være større end 0")
    @DecimalMax(value = "150", message = "Estimeret tid kan maksimalt være 150")
    private Double estimatedTime;

    @FutureOrPresent(message = "Deadline skal være en fremtidig dato")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    @NotNull(message = "Ressource er påkrævet")
    private Integer resourceId;
}
