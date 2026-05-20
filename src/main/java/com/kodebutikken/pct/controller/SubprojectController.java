package com.kodebutikken.pct.controller;

import com.kodebutikken.pct.dto.SubprojectForm;
import com.kodebutikken.pct.model.Resource;
import com.kodebutikken.pct.model.Subproject;
import com.kodebutikken.pct.model.Task;
import com.kodebutikken.pct.service.ProjectAccessService;
import com.kodebutikken.pct.service.SubprojectService;
import com.kodebutikken.pct.service.TaskService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/projects")
public class SubprojectController {
    private final SubprojectService subprojectService;
    private final TaskService taskService;
    private final ProjectAccessService projectAccessService;

    public SubprojectController(SubprojectService subprojectService,
                                TaskService taskService,
                                ProjectAccessService projectAccessService) {
        this.subprojectService = subprojectService;
        this.taskService = taskService;
        this.projectAccessService = projectAccessService;
    }

    @GetMapping("/{id}/subprojects")
    public String showSubprojects(@PathVariable int id, HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }
        projectAccessService.requireViewProject(id, userId);

        List<Subproject> subprojects = subprojectService.getAllSubProjects(id);
        model.addAttribute("subprojects", subprojects);
        model.addAttribute("projectId", id);
        return "subproject/index";
    }

    @GetMapping("/{id}/subprojects/create")
    public String showCreateSubprojectForm(@PathVariable int id, HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }
        projectAccessService.requireEditProject(id, userId);

        model.addAttribute("subprojectForm", new SubprojectForm());
        model.addAttribute("projectId", id);
        return "subproject/create";
    }

    @PostMapping("/{id}/subprojects/create")
    public String createSubproject(@PathVariable int id, @Valid @ModelAttribute("subprojectForm") SubprojectForm subprojectForm, BindingResult bindingResult, HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }
        projectAccessService.requireEditProject(id, userId);

        if (bindingResult.hasErrors()) {
            model.addAttribute("projectId", id);
            return "subproject/create";
        }

        try {
            subprojectService.createSubproject(id, subprojectForm);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("Error", e.getMessage());
            model.addAttribute("projectId", id);
            return "subproject/create";
        }
        return "redirect:/projects/" + id + "/subprojects";
    }

    @GetMapping("/subprojects/{id}/tasks")
    public String getTasksBySubproject(@PathVariable int id, HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }

        Integer projectId = subprojectService.getProjectIdBySubprojectId(id);
        projectAccessService.requireViewProject(projectId, userId);
        Subproject subproject = subprojectService.getSubprojectById(id);

        List<Task> tasks = taskService.getTasksBySubprojectId(id);
        List<Resource> resources = taskService.getResourcesForSubproject(id);

        model.addAttribute("tasks", tasks);
        model.addAttribute("subproject", subproject);
        model.addAttribute("canEditProject", projectAccessService.canEditProject(projectId, userId));
        model.addAttribute("canManageProject", projectAccessService.canManageProject(projectId, userId));
        model.addAttribute("canDeleteProject", projectAccessService.canDeleteProject(projectId, userId));
        model.addAttribute("resources", resources);
        return "subproject/spPage";
    }
}