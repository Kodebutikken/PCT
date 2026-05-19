package com.kodebutikken.pct.service;

import com.kodebutikken.pct.dto.SubprojectForm;
import com.kodebutikken.pct.model.Subproject;
import com.kodebutikken.pct.repository.SubprojectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubprojectService {
    private final SubprojectRepository subprojectRepository;

    public SubprojectService(SubprojectRepository subprojectRepository) {
        this.subprojectRepository = subprojectRepository;
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

    public Integer getProjectOwnerId(int subprojectId) {
        return subprojectRepository.getProjectOwnerId(subprojectId);
    }

    public Integer getProjectIdBySubprojectId(int subprojectId) {
        return subprojectRepository.getProjectIdBySubprojectId(subprojectId);
    }
}