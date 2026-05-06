package com.kodebutikken.pct.controller;

import com.kodebutikken.pct.model.Subproject;
import com.kodebutikken.pct.service.SubprojectService;
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
@RequestMapping("/projects")
public class SubprojectController {

    private final SubprojectService subprojectService;

    public SubprojectController(SubprojectService subprojectService) {
        this.subprojectService = subprojectService;
    }

    @GetMapping("/{id}/subprojects")
    public String showSubprojects(@PathVariable int id, HttpSession session, Model model) {
        if (session.getAttribute("profileId") == null) {
            return "redirect:/profile/login";
        }
        List<Subproject> subprojects = subprojectService.getAllProjects(id);
        model.addAttribute("subprojects", subprojects);
        model.addAttribute("projectId", id);
        return "subproject/index";
    }

    @GetMapping("/{id}/subprojects/create")
    public String showCreateSubprojectForm(@PathVariable int id, HttpSession session, Model model) {
        if (session.getAttribute("profileId") == null) {
            return "redirect:/profile/login";
        }
        model.addAttribute("subproject", new Subproject());
        model.addAttribute("projectId", id);
        return "subproject/create";
    }

    @PostMapping("/{id}/subprojects/create")
    public String createSubproject(@PathVariable int id, @ModelAttribute Subproject subproject, HttpSession session) {
        if (session.getAttribute("profileId") == null) {
            return "redirect:/profile/login";
        }
        subprojectService.createSubproject(id, subproject);
        return "redirect:/projects/" + id + "/subprojects";
    }
}