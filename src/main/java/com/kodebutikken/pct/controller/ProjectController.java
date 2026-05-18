package com.kodebutikken.pct.controller;

import com.kodebutikken.pct.dto.ProjectForm;
import com.kodebutikken.pct.model.Project;
import com.kodebutikken.pct.model.ProjectMember;
import com.kodebutikken.pct.model.Subproject;
import com.kodebutikken.pct.service.ProjectAccessService;
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
    private final ProjectAccessService projectAccessService;

    public ProjectController(ProjectService projectService,
                             SubprojectService subprojectService,
                             ProjectAccessService projectAccessService) {
        this.projectService = projectService;
        this.subprojectService = subprojectService;
        this.projectAccessService = projectAccessService;
    }

    @GetMapping()
    public String showProjects(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/users/login";
        }

        int userId = (int) session.getAttribute("userId");
        List<Project> projects = projectService.getProjectsAccessibleByUserId(userId);
        model.addAttribute("projects", projects);
        model.addAttribute("canCreateProjects", projectAccessService.canCreateProject(userId));
        return "project/projects";
    }

    @GetMapping("/create")
    public String showCreateProjectForm(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }
        if (!projectAccessService.canCreateProject(userId)) {
            return "redirect:/error";
        }

        model.addAttribute("projectForm", projectService.buildProjectForm(null, userId));
        model.addAttribute("isEdit", false);
        return "project/create";
    }

    @PostMapping("/create")
    public String createProject(
            @Valid @ModelAttribute ("projectForm") ProjectForm projectForm,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }
        if (!projectAccessService.canCreateProject(userId)) {
            return "redirect:/error";
        }
        if(bindingResult.hasErrors()) {
            projectService.repopulateProjectMembers(projectForm, userId);
            model.addAttribute("isEdit", false);
            return "project/create";
        }
        try {
            projectService.createProject(projectForm, userId);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("globalError", e.getMessage());
            projectService.repopulateProjectMembers(projectForm, userId);
            model.addAttribute("isEdit", false);
            return "project/create";
        }
        return "redirect:/projects";
    }

    @GetMapping("/{id}")
    public String showProject(@PathVariable int id, HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }
        if (!projectAccessService.canViewProject(id, userId)) {
            return "redirect:/error";
        }

        Project project = projectService.getProjectById(id);
        List<Subproject> subprojects = subprojectService.getAllSubProjects(id);
        List<ProjectMember> projectMembers = projectService.getProjectMembers(id);
        model.addAttribute("subprojects", subprojects);
        model.addAttribute("project", project);
        model.addAttribute("projectMembers", projectMembers);
        model.addAttribute("canEditProject", projectAccessService.canEditProject(id, userId));
        model.addAttribute("canManageProject", projectAccessService.canManageProject(id, userId));
        model.addAttribute("canDeleteProject", projectAccessService.canDeleteProject(id, userId));
        return "project/projectPage";
    }

    @GetMapping("/{id}/edit")
    public String showEditProjectForm(@PathVariable int id, HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }
        if (!projectAccessService.canManageProject(id, userId)) {
            return "redirect:/error";
        }

        model.addAttribute("projectForm", projectService.buildProjectForm(id, userId));
        model.addAttribute("projectId", id);
        model.addAttribute("isEdit", true);
        return "project/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateProject(@PathVariable int id,
                                @Valid @ModelAttribute("projectForm") ProjectForm projectForm,
                                BindingResult bindingResult,
                                HttpSession session,
                                Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }
        if (!projectAccessService.canManageProject(id, userId)) {
            return "redirect:/error";
        }

        if(bindingResult.hasErrors()) {
            projectService.repopulateProjectMembers(projectForm, userId);
            model.addAttribute("projectId", id);
            model.addAttribute("isEdit", true);
            return "project/edit";
        }

        try {
            projectService.updateProject(id, projectForm, userId);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("globalError", e.getMessage());
            projectService.repopulateProjectMembers(projectForm, userId);
            model.addAttribute("projectId", id);
            model.addAttribute("isEdit", true);
            return "project/edit";
        }

        return "redirect:/projects/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable int id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }
        projectService.deleteProject(id, userId);
        return "redirect:/projects";
    }

}
