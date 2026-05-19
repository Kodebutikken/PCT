package com.kodebutikken.pct.service;

import com.kodebutikken.pct.exception.InsufficientPermissionsException;
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

    public void requireCreateProject(int userId) {
        if (!canCreateProject(userId)) {
            throw new InsufficientPermissionsException("Du har ikke adgang til at oprette et nyt projekt");
        }
    }

    public boolean canViewProject(int projectId, int userId) {
        return projectRepository.isProjectOwner(projectId, userId)
                || projectRepository.isProjectMember(projectId, userId);
    }

    public void requireViewProject(int projectId, int userId) {
        if (!canViewProject(projectId, userId)) {
            throw new InsufficientPermissionsException("Du har ikke adgang til dette projekt");
        }
    }

    public boolean canEditProject(int projectId, int userId) {
        return projectRepository.isProjectOwner(projectId, userId)
                || projectRepository.hasProjectAccessLevel(projectId, userId,
                EnumSet.of(ProjectAccessLevel.EDITOR, ProjectAccessLevel.MANAGER));
    }

    public void requireEditProject(int projectId, int userId) {
        if (!canEditProject(projectId, userId)) {
            throw new InsufficientPermissionsException("Du har ikke adgang til at redigere dette projekt");
        }
    }

    public boolean canManageProject(int projectId, int userId) {
        return projectRepository.isProjectOwner(projectId, userId)
                || projectRepository.hasProjectAccessLevel(projectId, userId,
                EnumSet.of(ProjectAccessLevel.MANAGER));
    }

    public void requireManageProject(int projectId, int userId) {
        if (!canManageProject(projectId, userId)) {
            throw new InsufficientPermissionsException("Du har ikke adgang til at administrere dette projekt");
        }
    }

    public boolean canDeleteProject(int projectId, int userId) {
        return projectRepository.isProjectOwner(projectId, userId);
    }
}