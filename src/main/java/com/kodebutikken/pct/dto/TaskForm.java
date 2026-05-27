package com.kodebutikken.pct.dto;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class TaskForm {

    @NotBlank(message = "Titlen må ikke være tom")
    @Size(max = 255, message = "Titlen må ikke være længere end 255 tegn")
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
