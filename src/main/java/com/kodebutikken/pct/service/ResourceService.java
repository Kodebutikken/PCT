package com.kodebutikken.pct.service;

import com.kodebutikken.pct.dto.ResourceForm;
import com.kodebutikken.pct.model.Resource;
import com.kodebutikken.pct.repository.ResourceRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

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
        projectAccessService.requireEditResources(userId);

        Resource resource = new Resource();
        resource.setName(resourceForm.getName().trim());
        resource.setSkills(resourceForm.getSkills().trim());
        resource.setDailyWorkingHours(resourceForm.getDailyWorkingHours());
        resource.setHourlyWage(resourceForm.getHourlyWage());

        resourceRepository.save(resource);
    }

    public List<Resource> getResources() {
        return resourceRepository.getAllResources();
    }

    public Resource getResourceById(int id) {
        return resourceRepository.getResourceById(id);
    }

    public ResourceForm getResourceForm(int id) {
        Resource resource = resourceRepository.getResourceById(id);
        if (resource == null) {
            throw new IllegalArgumentException("Resource with id " + id + " not found");
        }

        ResourceForm form = new ResourceForm();
        form.setName(resource.getName());
        form.setSkills(resource.getSkills());
        form.setDailyWorkingHours(resource.getDailyWorkingHours());
        form.setHourlyWage(resource.getHourlyWage());

        return form;
    }

    public void editResource(@Valid ResourceForm resourceForm, Integer userId, Integer resourceId) {
        if(resourceForm == null) {
            throw new IllegalArgumentException("Resource cannot be null");
        }
        projectAccessService.requireEditResources(userId);

        Resource updatedResource = new Resource();
        updatedResource.setId(resourceId);
        updatedResource.setName(resourceForm.getName().trim());
        updatedResource.setSkills(resourceForm.getSkills().trim());
        updatedResource.setDailyWorkingHours(resourceForm.getDailyWorkingHours());
        updatedResource.setHourlyWage(resourceForm.getHourlyWage());

        resourceRepository.update(updatedResource);
    }

    public void deleteResource(int id, Integer userId) {
        projectAccessService.requireEditResources(userId);
        resourceRepository.delete(id);
    }
}
