package com.kodebutikken.pct.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStats {
    private int subprojectCount;
    private int taskCount;
    private double totalEstimatedHours;
    private long totalProjectedCost;
}
