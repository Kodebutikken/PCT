package com.kodebutikken.pct.controller;


import com.kodebutikken.pct.dto.TaskForm;
import com.kodebutikken.pct.service.ProjectAccessService;
import com.kodebutikken.pct.service.SubprojectService;
import com.kodebutikken.pct.service.TaskService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/projects/subprojects")
public class TaskController {
    private final TaskService taskService;
    private final SubprojectService subprojectService;
    private final ProjectAccessService projectAccessService;

    public TaskController(TaskService taskService,
                          SubprojectService subprojectService,
                          ProjectAccessService projectAccessService) {
        this.taskService = taskService;
        this.subprojectService = subprojectService;
        this.projectAccessService = projectAccessService;
    }

    @GetMapping("/{id}/tasks/create")
    public String showCreateTaskForm(@PathVariable int id, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if(userId == null) {
            return "redirect:/users/login";
        }

        Integer projectId = subprojectService.getProjectIdBySubprojectId(id);
        projectAccessService.requireEditProject(projectId, userId);

        model.addAttribute("taskForm", new TaskForm());
        model.addAttribute("subprojectId", id);
        return "task/create";
    }

    @PostMapping("/{id}/tasks/create")
    public String createTask(@Valid @ModelAttribute("taskForm")TaskForm taskForm, BindingResult bindingResult, @PathVariable int id, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }

        Integer projectId = subprojectService.getProjectIdBySubprojectId(id);
        projectAccessService.requireEditProject(projectId, userId);

        if (bindingResult.hasErrors()) {
            model.addAttribute("subprojectId", id);
            return "task/create";
        }

        try {
            taskService.createTask(taskForm, id, userId);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("Error", e.getMessage());
            model.addAttribute("subprojectId", id);
            return "task/create";
        }

        return "redirect:/projects/subprojects/" + id + "/tasks";
    }

    @PostMapping("/{pid}/task/{tid}/delete")
    public String deleteTask(@PathVariable int pid, @PathVariable int tid, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }

        taskService.deleteTask(pid, tid, userId);
        return "redirect:/projects/subprojects/" + pid + "/tasks";
    }
}
