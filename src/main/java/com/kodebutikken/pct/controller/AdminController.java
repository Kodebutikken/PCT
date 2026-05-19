package com.kodebutikken.pct.controller;

import com.kodebutikken.pct.dto.ResourceForm;
import com.kodebutikken.pct.service.ProjectAccessService;
import com.kodebutikken.pct.service.ResourceService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ResourceService resourceService;
    private final ProjectAccessService projectAccessService;

    public AdminController(ResourceService resourceService, ProjectAccessService projectAccessService) {
        this.resourceService = resourceService;
        this.projectAccessService = projectAccessService;
    }

    @GetMapping()
    public String showAdmin() {
        return "admin/dashboard";
    }

    @GetMapping("/resources/create")
    public String showCreateResourceForm(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }
        projectAccessService.requireCreateProject(userId);

        model.addAttribute("resourceForm", new ResourceForm());

        return "admin/create-resource";
    }

    @PostMapping("/resources/create")
    public String createResource(
            @Valid @ModelAttribute ("resourceForm") ResourceForm resourceForm,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }
        projectAccessService.requireCreateProject(userId);

        if (bindingResult.hasErrors()) {
            return "admin/create-resource";
        }

        try {
            resourceService.createResource(resourceForm, userId);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/create-resource";
        }

        return "redirect:/admin";
    }
}
