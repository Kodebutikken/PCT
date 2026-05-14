package com.kodebutikken.pct.controller;

import com.kodebutikken.pct.dto.SubprojectForm;
import com.kodebutikken.pct.model.Role;
import com.kodebutikken.pct.model.Subproject;
import com.kodebutikken.pct.model.Task;
import com.kodebutikken.pct.model.User;
import com.kodebutikken.pct.service.ProjectService;
import com.kodebutikken.pct.service.SubprojectService;
import com.kodebutikken.pct.service.TaskService;
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
public class SubprojectController {
    private final SubprojectService subprojectService;
    private final TaskService taskService;
    private final UserService userService;
    private final ProjectService projectService;

    public SubprojectController(SubprojectService subprojectService, TaskService taskService, UserService userService, ProjectService projectService) {
        this.subprojectService = subprojectService;
        this.taskService = taskService;
        this.userService = userService;
        this.projectService = projectService;
    }

    @GetMapping("/{id}/subprojects")
    public String showSubprojects(@PathVariable int id, HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/users/login";
        }

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

        User user = userService.getUserById(userId);
        if (user.getRole() != Role.PROJECT_MANAGER) {
            return "redirect:/error";
        }

        if (!projectService.isProjectOwner(id, userId)) {
            return "redirect:/error";
        }

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

        User user = userService.getUserById(userId);
        if (user.getRole() != Role.PROJECT_MANAGER) {
            return "redirect:/error";
        }

        if (!projectService.isProjectOwner(id, userId)) {
            return "redirect:/error";
        }

        if (bindingResult.hasErrors()) {model.addAttribute("projectId", id);
            return "subproject/create";
        }

        try {subprojectService.createSubproject(id, subprojectForm);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("Error", e.getMessage());
            model.addAttribute("projectId", id);
            return "subproject/create";
        }
        return "redirect:/projects/" + id + "/subprojects";
    }

    @GetMapping("/subprojects/{id}/tasks")
    public String getTasksBySubproject(@PathVariable int id, Model model) {
        List<Task> tasks = taskService.getTasksBySubprojectId(id);
        model.addAttribute("tasks", tasks);
        model.addAttribute("subprojectId", id);
        return "task/list";
    }
}