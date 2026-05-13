package com.kodebutikken.pct.controller;

import com.kodebutikken.pct.dto.ProjectForm;
import com.kodebutikken.pct.model.Project;
import com.kodebutikken.pct.model.Role;
import com.kodebutikken.pct.model.User;
import com.kodebutikken.pct.service.ProjectService;
import com.kodebutikken.pct.service.UserService;
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
    private final UserService userService;

    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
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

        if (session.getAttribute("userId") == null) {
            return "redirect:/users/login";
        }

        int userId = (int) session.getAttribute("userId");

        User user = userService.getUserById(userId);

        if (user.getRole() != Role.PROJECT_MANAGER) {
            return "redirect:/access-denied";
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

    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable int id, HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/users/login";
        }

        int userId = (int) session.getAttribute("userId");

        User user = userService.getUserById(userId);

        if (user.getRole() != Role.PROJECT_MANAGER) {
            return "redirect:/access-denied";
        }

        projectService.deleteProject(id, userId);

        return "redirect:/projects";
    }
}
