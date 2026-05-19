package com.kodebutikken.pct.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resource {
    private int id;
    private String name;
    private String compentencies;
    private int dailyWorkingHours;
    private double hourlyWage;
}
