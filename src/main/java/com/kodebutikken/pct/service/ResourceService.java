package com.kodebutikken.pct.service;

import com.kodebutikken.pct.dto.ResourceForm;
import com.kodebutikken.pct.model.Resource;
import com.kodebutikken.pct.repository.ResourceRepository;
import org.springframework.stereotype.Service;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ProjectAccessService projectAccessService;

    public ResourceService(ResourceRepository resourceRepository, ProjectAccessService projectAccessService) {
        this.resourceRepository = resourceRepository;
        this.projectAccessService = projectAccessService;
    }

    public void createResource(ResourceForm resourceForm, int userId) {

        if(resourceForm == null) {
            throw new IllegalArgumentException("Resource form cannot be null");
        }

        projectAccessService.requireCreateProject(userId);

        Resource resource = new Resource();
        resource.setName(resourceForm.getName().trim());
        resource.setCompentencies(resourceForm.getCompentencies().trim());
        resource.setDailyWorkingHours(resourceForm.getDailyWorkingHours());
        resource.setHourlyWage(resourceForm.getHourlyWage());

        resourceRepository.save(resource);
    }

}
