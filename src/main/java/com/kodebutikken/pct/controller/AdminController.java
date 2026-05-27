package com.kodebutikken.pct.controller;

import com.kodebutikken.pct.model.Resource;
import com.kodebutikken.pct.service.ProjectAccessService;
import com.kodebutikken.pct.service.ResourceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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


}
