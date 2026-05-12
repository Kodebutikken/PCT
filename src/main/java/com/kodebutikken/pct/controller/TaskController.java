package com.kodebutikken.pct.controller;


import com.kodebutikken.pct.dto.TaskForm;
import com.kodebutikken.pct.model.Task;
import com.kodebutikken.pct.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create")
    public String createTask(@Valid @ModelAttribute("taskform")TaskForm taskForm, BindingResult bindingResult, @RequestParam int subprojectId, Model model) {
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
}
