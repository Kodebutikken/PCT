package com.kodebutikken.pct.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMember {
    private int userId;
    private String userName;
    private String userEmail;
    private ProjectAccessLevel accessLevel;
}