package com.kodebutikken.pct.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProjectForm {
    @NotBlank(message = "Projekttitel må ikke være tom")
    @Size(max = 255, message = "Projekttitel må ikke være længere end 255 tegn")
    private String title;

    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deadline;

    private List<ProjectMemberForm> members = new ArrayList<>();

}
