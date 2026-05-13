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

        if(projectForm.getDeadline() != null && projectForm.getDeadline().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Deadline must be a future date");
        }

        Project project = new Project();
        project.setTitle(projectForm.getTitle());
        project.setDescription(projectForm.getDescription());
        project.setDeadline(projectForm.getDeadline());
        project.setCreatedBy(userId);

        projectRepository.save(project, userId);
    }

    public List<Project> getProjectsByUserId(int userId) {
        // Implementer logikken for at hente alle projekter for en given profil
        // Brug userId til at filtrere projekterne i databasen
        return projectRepository.getProjectsByUserId(userId);
    }

    public boolean isProjectOwner(int projectId, int userId) {
        // Implementer logikken for at tjekke om en given profil er ejer af et projekt
        // Brug projectId og userId til at verificere ejerskabet i databasen
        return projectRepository.isProjectOwner(projectId, userId);
    }
}
