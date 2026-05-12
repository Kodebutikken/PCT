package com.kodebutikken.pct.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping()
    public String showIndex(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/projects";
        }
        return "index";
    }

    @GetMapping("/error")
    public String showError() {
        return "error";
    }
}
