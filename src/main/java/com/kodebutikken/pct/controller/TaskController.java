package com.kodebutikken.pct.controller;


import com.kodebutikken.pct.model.Task;
import com.kodebutikken.pct.service.TaskService;
import org.springframework.stereotype.Controller;
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
    public String createTask(@ModelAttribute Task task, @RequestParam int subprojectId) {
        task.setSubprojectId(subprojectId);
        taskService.createTask(task);
        return "redirect:/subprojects/" + subprojectId;
    }
}
