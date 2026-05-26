package com.kodebutikken.pct.controller;

import com.kodebutikken.pct.dto.LoginForm;
import com.kodebutikken.pct.dto.RegisterForm;
import com.kodebutikken.pct.model.Role;
import com.kodebutikken.pct.model.User;
import com.kodebutikken.pct.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @InjectMocks
    private UserController userController;

    @Test
    void register_redirectsToLogin_onSuccess() {
        RegisterForm form = new RegisterForm();
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = userController.register(form, bindingResult, model);

        assertEquals("redirect:/users/login", view);
    }

    @Test
    void register_returnsRegisterView_whenValidationFails() {
        RegisterForm form = new RegisterForm();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = userController.register(form, bindingResult, model);

        assertEquals("auth/register", view);
    }

    @Test
    void register_returnsRegisterView_whenEmailAlreadyExists() {
        RegisterForm form = new RegisterForm();
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new IllegalArgumentException("Email already exists"))
                .when(userService).register(form);

        String view = userController.register(form, bindingResult, model);

        assertEquals("auth/register", view);
        verify(model).addAttribute("error", "Email already exists");
    }

    @Test
    void login_redirectsToProjects_onSuccess() {
        User bruger = new User(1, "Anders", "anders@example.com", "hash", Role.DEVELOPER);
        LoginForm form = new LoginForm();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.authenticate(form)).thenReturn(bruger);

        String view = userController.login(form, bindingResult, model, session);

        assertEquals("redirect:/projects", view);
        verify(session).setAttribute("userId", 1);
    }

    @Test
    void login_returnsLoginView_whenCredentialsAreWrong() {
        LoginForm form = new LoginForm();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.authenticate(form)).thenReturn(null);

        String view = userController.login(form, bindingResult, model, session);

        assertEquals("auth/login", view);
        verify(model).addAttribute("error", "Forkert brugernavn eller adgangskode");
    }

    @Test
    void login_returnsLoginView_whenValidationFails() {
        LoginForm form = new LoginForm();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = userController.login(form, bindingResult, model, session);

        assertEquals("auth/login", view);
        verifyNoInteractions(userService);
    }

    @Test
    void logout_invalidatesSession_andRedirectsToLogin() {
        String view = userController.logout(session);

        verify(session).invalidate();
        assertEquals("redirect:/", view);
    }
}