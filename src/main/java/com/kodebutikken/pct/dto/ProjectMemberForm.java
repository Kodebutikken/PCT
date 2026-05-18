package com.kodebutikken.pct.dto;

import com.kodebutikken.pct.model.ProjectAccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProjectMemberForm {
    private int userId;
    private String userName;
    private String userEmail;
    private boolean assigned;
    private ProjectAccessLevel accessLevel = ProjectAccessLevel.VIEWER;
}