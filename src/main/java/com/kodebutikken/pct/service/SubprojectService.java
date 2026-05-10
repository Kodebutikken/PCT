package com.kodebutikken.pct.service;

import com.kodebutikken.pct.model.Project;
import com.kodebutikken.pct.model.Subproject;
import com.kodebutikken.pct.repository.ProjectRepository;
import com.kodebutikken.pct.repository.SubprojectRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SubprojectService {

    private final SubprojectRepository subprojectRepository;
    private final ProjectRepository projectRepository;

    public SubprojectService(SubprojectRepository subprojectRepository, ProjectRepository projectRepository) {
        this.subprojectRepository = subprojectRepository;
        this.projectRepository = projectRepository;
    }

    public void createSubproject(int projectId, Subproject subproject) {
        Project project = projectRepository.getProjectById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Projekt med id " + projectId + " eksisterer ikke");
        }

        if (subproject.getTitel() == null || subproject.getTitel().isBlank()) {
            throw new IllegalArgumentException("Titel må ikke være tom");
        }

        if (subproject.getDeadline() == null) {
            throw new IllegalArgumentException("Deadline må ikke være tom");
        }

        if (subproject.getDeadline().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Deadline skal være en fremtidig dato");
        }

        subproject.setProjectId(projectId);
        subprojectRepository.createSubproject(subproject);
    }

    public List<Subproject> getAllSubProjects(int projectId) {
        Project project = projectRepository.getProjectById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Projekt med id " + projectId + " eksisterer ikke");
        }

        return subprojectRepository.getSubprojectsByProjectId(projectId);
    }
}