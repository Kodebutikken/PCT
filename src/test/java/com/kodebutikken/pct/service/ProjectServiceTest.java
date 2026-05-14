package com.kodebutikken.pct.service;

import com.kodebutikken.pct.dto.ProjectForm;
import com.kodebutikken.pct.model.Project;
import com.kodebutikken.pct.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;


    @Test
    void createProject_succes() {
        ProjectForm projectForm = new ProjectForm();
        projectForm.setTitle("Test Project");
        projectForm.setDescription("This is a test project.");
        projectForm.setDeadline(LocalDate.now().plusDays(7));

        int profileId = 1;

        projectService.createProject(projectForm, profileId);

        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);

        verify(projectRepository).save(projectCaptor.capture(), eq(profileId));

        Project capturedProject = projectCaptor.getValue();

        assertEquals("Test Project", capturedProject.getTitle());
        assertEquals("This is a test project.", capturedProject.getDescription());
        assertEquals(profileId, capturedProject.getCreatedBy());
    }

    @Test
    void createProject_shouldThrowException_whenDeadlineIsPast() {
        ProjectForm projectForm = new ProjectForm();
        projectForm.setTitle("Test Project");
        projectForm.setDeadline(LocalDate.now().minusDays(1));

        assertThrows(IllegalArgumentException.class, () -> {
            projectService.createProject(projectForm, 1);
        });

        verify(projectRepository, never()).save(any(), anyInt());
    }

    @Test
    void createProject_shouldThrowException_whenTitleIsWhitespace() {
        ProjectForm projectForm = new ProjectForm();
        projectForm.setTitle("   ");
        projectForm.setDeadline(LocalDate.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> {
            projectService.createProject(projectForm, 1);
        });

        verify(projectRepository, never()).save(any(), anyInt());
    }

    @Test
    void getProjectsByUserId() {
        List<Project> mockProjects = List.of(
                new Project(1, "Project 1", "Description 1", LocalDate.now().plusDays(5), 1),
                new Project(2, "Project 2", "Description 2", LocalDate.now().plusDays(10), 1)
        );

        when(projectRepository.getProjectsByUserId(1)).thenReturn(mockProjects);

        List<Project> result = projectService.getProjectsByUserId(1);

        assertEquals(2, result.size());
        assertEquals("Project 1", result.get(0).getTitle());
        assertEquals("Project 2", result.get(1).getTitle());

        verify(projectRepository).getProjectsByUserId(1);
    }

    @Test
    void deleteProject_shouldDeleteProject_whenUserIsOwner() {
        int projectId = 1;
        int userId = 1;

        when(projectRepository.isProjectOwner(projectId, userId)).thenReturn(true);

        projectService.deleteProject(projectId, userId);

        verify(projectRepository).delete(projectId, userId);
    }

    @Test
    void deleteProject_shouldThrowException_whenUserIsNotOwner() {
        int projectId = 1;
        int userId = 2;

        when(projectRepository.isProjectOwner(projectId, userId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            projectService.deleteProject(projectId, userId);
        });

        verify(projectRepository, never()).delete(anyInt(), anyInt());
    }


}