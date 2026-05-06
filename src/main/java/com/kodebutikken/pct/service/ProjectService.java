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

    public void createProject(ProjectForm projectForm, int profileId) {
        // Implementer logikken for at oprette et projekt i databasen
        // Brug profileId til at knytte projektet til den rigtige profil
        // Du kan bruge en repository eller DAO til at håndtere databaseoperationerne

        if(projectForm.getDeadline() != null && projectForm.getDeadline().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Deadline must be a future date");
        }

        Project project = new Project();
        project.setTitle(projectForm.getTitle());
        project.setDescription(projectForm.getDescription());
        project.setDeadline(projectForm.getDeadline());
        project.setCreatedBy(profileId);

        projectRepository.save(project, profileId);
    }

    public List<Project> getProjectsByProfileId(int profileId) {
        // Implementer logikken for at hente alle projekter for en given profil
        // Brug profileId til at filtrere projekterne i databasen
        return projectRepository.getProjectsByProfileId(profileId);
    }
}
