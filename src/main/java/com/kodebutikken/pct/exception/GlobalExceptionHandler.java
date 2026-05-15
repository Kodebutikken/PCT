package com.kodebutikken.pct.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DatabaseOperationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDatabaseOperationException(DatabaseOperationException e, Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("error", "Databasefejl");
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleProjectNotFoundException(ProjectNotFoundException e, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("error", "Ikke fundet");
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleTaskNotFoundException(TaskNotFoundException e, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("error", "Ikke fundet");
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleUnauthorizedException(UnauthorizedException e, Model model) {
        model.addAttribute("status", 401);
        model.addAttribute("error", "Ingen adgang");
        model.addAttribute("message", e.getMessage());
        return "error";
    }
}
