package com.kodebutikken.pct.service;

import com.kodebutikken.pct.dto.SubprojectForm;
import com.kodebutikken.pct.model.Project;
import com.kodebutikken.pct.model.Subproject;
import com.kodebutikken.pct.repository.ProjectRepository;
import com.kodebutikken.pct.repository.SubprojectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubprojectService {

    private final SubprojectRepository subprojectRepository;
    private final ProjectRepository projectRepository;

    public SubprojectService(SubprojectRepository subprojectRepository, ProjectRepository projectRepository) {
        this.subprojectRepository = subprojectRepository;
        this.projectRepository = projectRepository;
    }

    public void createSubproject(int projectId, SubprojectForm form) {
        Project project = projectRepository.getProjectById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Projekt med id " + projectId + " eksisterer ikke");
        }

        Subproject subproject = new Subproject();
        subproject.setTitle(form.getTitle());
        subproject.setDescription(form.getDescription());
        subproject.setDeadline(form.getDeadline());
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