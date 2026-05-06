package com.kodebutikken.pct.service;

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

    public void createSubproject(int projectId, Subproject subproject) {
        subproject.setProjectId(projectId);
        subprojectRepository.createSubproject(subproject);
    }

    public List<Subproject> getAllProjects(int projectId) {
        return subprojectRepository.getSubprojectsByProjectId(projectId);
    }
}