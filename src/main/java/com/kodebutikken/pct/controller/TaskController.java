package com.kodebutikken.pct.controller;


import com.kodebutikken.pct.dto.TaskForm;
import com.kodebutikken.pct.service.TaskService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create")
    public String createTask(@Valid @ModelAttribute("taskForm")TaskForm taskForm, BindingResult bindingResult, @RequestParam int subprojectId, Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("subprojectId", subprojectId);
            return "task/create";
        }

        int profileId = (int) session.getAttribute("profileId");

        try {
            taskService.createTask(taskForm, subprojectId, profileId);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("Error", e.getMessage());
            model.addAttribute("subprojectId", subprojectId);
            return "task/create";
        }

        return "redirect:/subprojects/" + subprojectId + "/tasks";
    }

    @GetMapping("/create")
    public String showCreateTaskForm(@RequestParam int subprojectId, Model model, HttpSession session) {
        if(session.getAttribute("profileId") == null) {
            return "redirect:/users/login";
        }
        model.addAttribute("taskForm", new TaskForm());
        model.addAttribute("subprojectId", subprojectId);
        return "task/create";
    }
}
