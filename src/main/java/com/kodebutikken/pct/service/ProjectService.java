package com.kodebutikken.pct.service;

import com.kodebutikken.pct.dto.ProjectForm;
import com.kodebutikken.pct.model.Project;
import com.kodebutikken.pct.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void createProject(ProjectForm projectForm, int userId) {
        // Implementer logikken for at oprette et projekt i databasen
        // Brug userId til at knytte projektet til den rigtige profil
        // Du kan bruge en repository eller DAO til at håndtere databaseoperationerne

        if(!isValidProjectForm(projectForm)) {
            throw new IllegalArgumentException("Invalid project form data");
        }

        Project project = new Project();
        project.setTitle(projectForm.getTitle());
        project.setDescription(projectForm.getDescription());
        project.setDeadline(projectForm.getDeadline());
        project.setCreatedBy(userId);

        projectRepository.save(project, userId);
    }

    public List<Project> getProjectsByUserId(int userId) {

        return projectRepository.getProjectsByUserId(userId);
    }

    public void deleteProject(int id, int userId) {

        if(!isProjectOwner(id, userId)) {
            throw new IllegalArgumentException("User does not have permission to delete this project");
        }
        projectRepository.delete(id, userId);
    }


    private boolean isValidProjectForm(ProjectForm projectForm) {
        if (projectForm.getTitle() == null || projectForm.getTitle().isEmpty()) {
            return false;
        }
        return projectForm.getDeadline() == null || !projectForm.getDeadline().isBefore(java.time.LocalDate.now());
    }

    private boolean isProjectOwner(int projectId, int userId) {
        List<Project> projects = projectRepository.getProjectsByUserId(userId);
        return projects.stream().anyMatch(project -> project.getId() == projectId);
    }
}
