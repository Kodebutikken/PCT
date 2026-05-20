package com.kodebutikken.pct.controller;

import com.kodebutikken.pct.dto.ResourceForm;
import com.kodebutikken.pct.model.Resource;
import com.kodebutikken.pct.service.ProjectAccessService;
import com.kodebutikken.pct.service.ResourceService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public String showAdmin(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }
        projectAccessService.requireAdmin(userId);

        List<Resource> resources = resourceService.getResources();

        model.addAttribute("resources", resources);

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
            @Valid @ModelAttribute("resourceForm") ResourceForm resourceForm,
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

    @GetMapping("/resources/{id}/edit")
    public String showEditResourceForm(@PathVariable int id, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }
        projectAccessService.requireCreateProject(userId);

        Resource resource = resourceService.getResourceById(id);
        if (resource == null) {
            return "redirect:/admin";
        }

        model.addAttribute("resourceForm", resourceService.getResourceForm(id));
        model.addAttribute("resourceId", id);

        return "admin/edit-resource";
    }

    @PostMapping("/resources/{id}/edit")
    public String editResource(
            @PathVariable int id,
            @Valid @ModelAttribute("resourceForm") ResourceForm resourceForm,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }
        projectAccessService.requireCreateProject(userId);

        if (bindingResult.hasErrors()) {
            model.addAttribute("resourceId", id);
            model.addAttribute("resourceForm", resourceForm);
            return "admin/edit-resource";
        }

        try {
            resourceService.editResource(resourceForm, userId, id);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("resourceId", id);
            return "admin/edit-resource";
        }

        return "redirect:/admin";
    }

    @PostMapping("/resources/{id}/delete")
    public String deleteResource(@PathVariable int id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }

        resourceService.deleteResource(id, userId);
        return "redirect:/admin";
    }
}
