package com.kodebutikken.pct.service;

import com.kodebutikken.pct.dto.SubprojectForm;
import com.kodebutikken.pct.exception.ProjectNotFoundException;
import com.kodebutikken.pct.model.Subproject;
import com.kodebutikken.pct.repository.SubprojectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubprojectService {
    private final SubprojectRepository subprojectRepository;
    private final ProjectAccessService projectAccessService;

    public SubprojectService(SubprojectRepository subprojectRepository, ProjectAccessService projectAccessService) {
        this.subprojectRepository = subprojectRepository;
        this.projectAccessService = projectAccessService;
    }

    public void createSubproject(int projectId, SubprojectForm form) {
        Subproject subproject = new Subproject();
        subproject.setTitle(form.getTitle());
        subproject.setDescription(form.getDescription());
        subproject.setDeadline(form.getDeadline());
        subproject.setProjectId(projectId);

        subprojectRepository.createSubproject(subproject);
    }

    public List<Subproject> getAllSubProjects(int projectId) {
        return subprojectRepository.getSubprojectsByProjectId(projectId);
    }

    public Subproject getSubprojectById(int id) {
        return subprojectRepository.getSubprojectById(id);
    }

    public boolean existsById(int id) {
        return subprojectRepository.existsById(id);
    }

    public Integer getProjectIdBySubprojectId(int subprojectId) {
        return subprojectRepository.getProjectIdBySubprojectId(subprojectId);
    }

    public String getProjectNameBySubprojectId(int subprojectId) {
        return subprojectRepository.getProjectTitleById(subprojectId);
    }

    public SubprojectForm getEditForm(int id) {
        Subproject subproject = subprojectRepository.getSubprojectById(id);
        SubprojectForm form = new SubprojectForm();
        form.setTitle(subproject.getTitle());
        form.setDescription(subproject.getDescription());
        form.setDeadline(subproject.getDeadline());
        return form;
    }

    public void updateSubproject(int id, SubprojectForm form, int userId) {
        if (!subprojectRepository.existsById(id)) {
            throw new ProjectNotFoundException("Delprojekt findes ikke");
        }
        int projectId = getProjectIdBySubprojectId(id);
        projectAccessService.requireEditProject(projectId, userId);
        Subproject subproject = subprojectRepository.getSubprojectById(id);
        subproject.setTitle(form.getTitle());
        subproject.setDescription(form.getDescription());
        subproject.setDeadline(form.getDeadline());
        subprojectRepository.updateSubproject(subproject);
    }

    public void deleteSubproject(int id, int userId) {
        if (!subprojectRepository.existsById(id)) {
            throw new ProjectNotFoundException("Delprojekt findes ikke");
        }
        int projectId = getProjectIdBySubprojectId(id);
        projectAccessService.requireEditProject(projectId, userId);

        subprojectRepository.deleteProject(id);
    }
}