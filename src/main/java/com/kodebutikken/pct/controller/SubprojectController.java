package com.kodebutikken.pct.controller;

import com.kodebutikken.pct.dto.SubprojectForm;
import com.kodebutikken.pct.model.Subproject;
import com.kodebutikken.pct.model.Task;
import com.kodebutikken.pct.service.SubprojectService;
import com.kodebutikken.pct.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/subprojects")
public class SubprojectController {

    private final SubprojectService subprojectService;
    private final TaskService taskService;



    public SubprojectController(SubprojectService subprojectService, TaskService taskService) {
        this.subprojectService = subprojectService;
        this.taskService = taskService;
    }

    @GetMapping("/{id}/subprojects")
    public String showSubprojects(@PathVariable int id, HttpSession session, Model model) {
        if (session.getAttribute("profileId") == null) {
            return "redirect:/users/login";
        }
        List<Subproject> subprojects = subprojectService.getAllSubProjects(id);
        model.addAttribute("subprojects", subprojects);
        model.addAttribute("projectId", id);
        return "subproject/index";
    }

    @GetMapping("/{id}/subprojects/create")
    public String showCreateSubprojectForm(@PathVariable int id, HttpSession session, Model model) {
        if (session.getAttribute("profileId") == null) {
            return "redirect:/users/login";
        }
        model.addAttribute("subprojectForm", new SubprojectForm());
        model.addAttribute("projectId", id);
        return "subproject/create";
    }

    @PostMapping("/{id}/subprojects/create")
    public String createSubproject(@PathVariable int id, @ModelAttribute SubprojectForm subprojectForm, HttpSession session) {
        if (session.getAttribute("profileId") == null) {
            return "redirect:/users/login";
        }
        subprojectService.createSubproject(id, subprojectForm);
        return "redirect:/projects/" + id + "/subprojects";
    }

    @GetMapping("/{id}/tasks")
    public String getTasksBySubproject(@PathVariable int id, Model model) {

        List<Task> tasks = taskService.getTasksBySubprojectId(id);

        model.addAttribute("tasks", tasks);
        model.addAttribute("subprojectId", id);

        return "task/list";
    }
}


