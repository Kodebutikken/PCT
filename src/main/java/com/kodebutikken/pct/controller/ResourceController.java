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

@Controller
@RequestMapping("/resources")
public class ResourceController {
    private final ResourceService resourceService;
    private final ProjectAccessService projectAccessService;

    public ResourceController(ResourceService resourceService, ProjectAccessService projectAccessService) {
        this.resourceService = resourceService;
        this.projectAccessService = projectAccessService;
    }

    @GetMapping
    public String showResources(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/users/login";
        }
        model.addAttribute("resources", resourceService.getResources());
        model.addAttribute("canEditResources", projectAccessService.canEditResources((Integer) session.getAttribute("userId")));
        return "resource/resources";
    }

    @GetMapping("/create")
    public String showCreateResourceForm(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }
        projectAccessService.requireEditResources(userId);

        model.addAttribute("resourceForm", new ResourceForm());

        return "resource/create";
    }

    @PostMapping("/create")
    public String createResource(
            @Valid @ModelAttribute("resourceForm") ResourceForm resourceForm,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }

        if (bindingResult.hasErrors()) {
            return "resource/create";
        }

        try {
            resourceService.createResource(resourceForm, userId);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "resource/create";
        }

        return "redirect:/resources";
    }

    @GetMapping("/{id}/edit")
    public String showEditResourceForm(@PathVariable int id, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }
        projectAccessService.requireEditResources(userId);

        Resource resource = resourceService.getResourceById(id);
        if (resource == null) {
            return "redirect:/resources";
        }

        model.addAttribute("resourceForm", resourceService.getResourceForm(id));
        model.addAttribute("resourceId", id);

        return "resource/edit";
    }

    @PostMapping("/{id}/edit")
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

        if (bindingResult.hasErrors()) {
            model.addAttribute("resourceId", id);
            model.addAttribute("resourceForm", resourceForm);
            return "resource/edit";
        }

        try {
            resourceService.editResource(resourceForm, userId, id);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("resourceId", id);
            return "resource/edit";
        }

        return "redirect:/resources";
    }

    @PostMapping("/{id}/delete")
    public String deleteResource(@PathVariable int id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }

        resourceService.deleteResource(id, userId);
        return "redirect:/resources";
    }
}
