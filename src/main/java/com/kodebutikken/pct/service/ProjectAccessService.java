package com.kodebutikken.pct.service;

import com.kodebutikken.pct.model.ProjectAccessLevel;
import com.kodebutikken.pct.model.Role;
import com.kodebutikken.pct.model.User;
import com.kodebutikken.pct.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public class ProjectAccessService {
    private final ProjectRepository projectRepository;
    private final UserService userService;

    public ProjectAccessService(ProjectRepository projectRepository, UserService userService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
    }

    public boolean canCreateProject(int userId) {
        User user = userService.getUserById(userId);
        return user.getRole() == Role.PROJECT_MANAGER;
    }

    public boolean canViewProject(int projectId, int userId) {
        return projectRepository.isProjectOwner(projectId, userId)
                || projectRepository.isProjectMember(projectId, userId);
    }

    public boolean canEditProject(int projectId, int userId) {
        return projectRepository.isProjectOwner(projectId, userId)
                || projectRepository.hasProjectAccessLevel(projectId, userId,
                EnumSet.of(ProjectAccessLevel.EDITOR, ProjectAccessLevel.MANAGER));
    }

    public boolean canManageProject(int projectId, int userId) {
        return projectRepository.isProjectOwner(projectId, userId)
                || projectRepository.hasProjectAccessLevel(projectId, userId,
                EnumSet.of(ProjectAccessLevel.MANAGER));
    }

    public boolean canDeleteProject(int projectId, int userId) {
        return projectRepository.isProjectOwner(projectId, userId);
    }
}