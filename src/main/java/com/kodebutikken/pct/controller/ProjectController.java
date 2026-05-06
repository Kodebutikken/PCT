package com.kodebutikken.pct.controller;

import com.kodebutikken.pct.model.Project;
import com.kodebutikken.pct.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping()
    public String showProjects(HttpSession session, Model model) {
        if (session.getAttribute("profileId") == null) {
            return "redirect:/profile/login";
        }

        List<Project> projects = projectService.getProjectsByProfileId((int) session.getAttribute("profileId"));
        model.addAttribute("projects", projects);

        return "projects";
    }

    @GetMapping("/create")
    public String showCreateProjectForm(HttpSession session, Model model) {
        if (session.getAttribute("profileId") == null) {
            return "redirect:/profile/login";
        }

        int profileId = (int) session.getAttribute("profileId");

        model.addAttribute("project", new Project());
        model.addAttribute("profileId", profileId);

        return "project/create";
    }

    @PostMapping("/create")
    public String createProject(@ModelAttribute Project project, HttpSession session) {
        if (session.getAttribute("profileId") == null) {
            return "redirect:/profile/login";
        }

        int profileId = (int) session.getAttribute("profileId");
        projectService.createProject(project, profileId);

        return "redirect:/projects";
    }

}
