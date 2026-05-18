package com.kodebutikken.pct.controller;

import com.kodebutikken.pct.dto.ProjectForm;
import com.kodebutikken.pct.model.Project;
import com.kodebutikken.pct.model.Subproject;
import com.kodebutikken.pct.service.ProjectService;
import com.kodebutikken.pct.service.SubprojectService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final SubprojectService subprojectService;

    public ProjectController(ProjectService projectService, SubprojectService subprojectService) {
        this.projectService = projectService;
        this.subprojectService = subprojectService;
    }

    @GetMapping()
    public String showProjects(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/users/login";
        }
        List<Project> projects = projectService.getProjectsByUserId((int) session.getAttribute("userId"));
        model.addAttribute("projects", projects);
        return "project/projects";
    }

    @GetMapping("/create")
    public String showCreateProjectForm(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/users/login";
        }
        model.addAttribute("projectForm", new ProjectForm());
        return "project/create";
    }

    @PostMapping("/create")
    public String createProject(
            @Valid @ModelAttribute ("projectForm") ProjectForm projectForm,
            BindingResult bindingResult,
            HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        if (session.getAttribute("userId") == null) {
            return "redirect:/users/login";
        }
        if(bindingResult.hasErrors()) {
            return "project/create";
        }
        try {
            projectService.createProject(projectForm, userId);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("globalError", e.getMessage());
            return "project/create";
        }
        return "redirect:/projects";
    }

    @GetMapping("/{id}")
    public String showProject(@PathVariable int id, HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/users/login";
        }
        Project project = projectService.getProjectById(id);
        List<Subproject> subprojects = subprojectService.getAllSubProjects(id);
        model.addAttribute("subprojects", subprojects);
        model.addAttribute("project", project);
        return "project/projectPage";
    }

    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable int id, HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        if (session.getAttribute("userId") == null) {
            return "redirect:/users/login";
        }
        projectService.deleteProject(id, userId);
        return "redirect:/projects";
    }
}
