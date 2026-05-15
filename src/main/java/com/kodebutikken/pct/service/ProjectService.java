package com.kodebutikken.pct.service;

import com.kodebutikken.pct.dto.ProjectForm;
import com.kodebutikken.pct.exception.UnauthorizedException;
import com.kodebutikken.pct.model.Project;
import com.kodebutikken.pct.model.Role;
import com.kodebutikken.pct.model.User;
import com.kodebutikken.pct.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService;

    public ProjectService(ProjectRepository projectRepository, UserService userService) {
        this.userService = userService;
        this.projectRepository = projectRepository;
    }

    public void createProject(ProjectForm projectForm, int userId) {
        if(projectForm == null) {
            throw new IllegalArgumentException("Project form cannot be null");
        }
        String validationError = isValidProjectForm(projectForm);
        if(validationError != null) {
            throw new IllegalArgumentException(validationError);
        }
        User user = userService.getUserById(userId);
        if (user.getRole() != Role.PROJECT_MANAGER) {
            throw new UnauthorizedException("Du har ikke adgang til at oprette projekter");
        }
        Project project = new Project();
        project.setTitle(projectForm.getTitle().trim());
        project.setDescription(projectForm.getDescription() != null ? projectForm.getDescription().trim() : null);
        project.setDeadline(projectForm.getDeadline());
        project.setCreatedBy(userId);
        projectRepository.save(project, userId);
    }

    public List<Project> getProjectsByUserId(int userId) {
        return projectRepository.getProjectsByUserId(userId);
    }

    @Transactional
    public void deleteProject(int id, int userId) {
        if(!isProjectOwner(id, userId)) {
            throw new UnauthorizedException("Du har ikke adgang til at slette dette projekt");
        }
        projectRepository.delete(id, userId);
    }


    private String isValidProjectForm(ProjectForm projectForm) {
        String title = projectForm.getTitle();
        if (title == null || title.trim().isEmpty()) {
            return "Projektet skal have en titel";
        }
        if(projectForm.getDeadline() == null) {
            return "Deadline er påkrævet";
        }
        if(projectForm.getDeadline().isBefore(java.time.LocalDate.now())) {
            return "Deadline skal være en fremtidig dato";
        }
        return null;
    }

    private boolean isProjectOwner(int projectId, int userId) {
        return projectRepository.isProjectOwner(projectId, userId);
    }
}
