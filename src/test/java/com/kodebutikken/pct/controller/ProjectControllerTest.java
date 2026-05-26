package com.kodebutikken.pct.controller;

import com.kodebutikken.pct.dto.ProjectForm;
import com.kodebutikken.pct.exception.InsufficientPermissionsException;
import com.kodebutikken.pct.model.Project;
import com.kodebutikken.pct.model.Role;
import com.kodebutikken.pct.service.ProjectAccessService;
import com.kodebutikken.pct.service.ProjectService;
import com.kodebutikken.pct.service.SubprojectService;
import com.kodebutikken.pct.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private SubprojectService subprojectService;

    @MockitoBean
    private ProjectAccessService projectAccessService;

    @MockitoBean
    private TaskService taskService;

    @Test
    void showProjects() throws Exception {
        List<Project> projects = List.of(
                new Project(1, "Project 1", "Description 1", LocalDate.now(), 1),
                new Project(2, "Project 2", "Description 2", LocalDate.now(), 1)
                );

        when(projectService.getProjectsAccessibleByUserId(1)).thenReturn(projects);
        when(projectAccessService.canCreateProject(1)).thenReturn(true);

        mockMvc.perform(get("/projects").sessionAttr("userId", 1))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("projects"))
                .andExpect(model().attribute("projects", projects));
    }


    @Test
    void createProject_succes() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1);
        session.setAttribute("role", Role.PROJECT_MANAGER);

        when(projectAccessService.canCreateProject(1)).thenReturn(true);

        mockMvc.perform(post("/projects/create")
                        .session(session)
                        .param("title", "New Project")
                        .param("description", "Project Description")
                        .param("dueDate", LocalDate.now().plusDays(7).toString()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors());

        ArgumentCaptor<ProjectForm> projectCaptor = ArgumentCaptor.forClass(ProjectForm.class);
        verify(projectService).createProject(projectCaptor.capture(), eq(1));

        ProjectForm capturedProject = projectCaptor.getValue();
        assertEquals("New Project", capturedProject.getTitle());
    }

    @Test
    void createProject_fail() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1);
        session.setAttribute("role", Role.DEVELOPER);

        doThrow(new InsufficientPermissionsException("Du har ikke adgang til at oprette et nyt projekt"))
                .when(projectAccessService).requireCreateProject(1);


        mockMvc.perform(post("/projects/create")
                        .session(session)
                        .param("title", "New Project")
                        .param("description", "Project Description")
                        .param("dueDate", LocalDate.now().plusDays(7).toString()))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("status", 403));

        verify(projectService, never()).createProject(any(ProjectForm.class), anyInt());

    }


}
