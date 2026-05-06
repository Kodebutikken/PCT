package com.kodebutikken.pct.service;

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

    public void createProject(Project project, int profileId) {
        // Implementer logikken for at oprette et projekt i databasen
        // Brug profileId til at knytte projektet til den rigtige profil
        // Du kan bruge en repository eller DAO til at håndtere databaseoperationerne
        projectRepository.createProject(project, profileId);
    }

    public List<Project> getProjectsByProfileId(int profileId) {
        // Implementer logikken for at hente alle projekter for en given profil
        // Brug profileId til at filtrere projekterne i databasen
        return projectRepository.getProjectsByProfileId(profileId);
    }
}
