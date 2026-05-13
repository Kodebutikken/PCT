package com.kodebutikken.pct.service;

import com.kodebutikken.pct.dto.LoginForm;
import com.kodebutikken.pct.dto.RegisterForm;
import com.kodebutikken.pct.model.Role;
import com.kodebutikken.pct.model.User;
import com.kodebutikken.pct.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    private UserService userService;

    @Test
    void register_savesUserWithHashedPassword() {
        RegisterForm form = new RegisterForm();
        form.setName("Anders And");
        form.setEmail("anders@example.com");
        form.setPassword("password123");

        when(userRepository.emailExists("anders@example.com")).thenReturn(false);

        userService.register(form);

        verify(userRepository).createUser(argThat(user ->
                user.getName().equals("Anders And") &&
                        user.getEmail().equals("anders@example.com") &&
                        !user.getPasswordHash().equals("password123") && // ikke gemt i klartekst
                        user.getRole() == Role.DEVELOPER
        ));
    }

    @Test
    void register_throwsException_whenEmailAlreadyExists() {
        RegisterForm form = new RegisterForm();
        form.setName("Anders And");
        form.setEmail("anders@example.com");
        form.setPassword("password123");

        when(userRepository.emailExists("anders@example.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.register(form)
        );
        assertEquals("Email already exists", ex.getMessage());

        verify(userRepository, never()).createUser(any());
    }

    @Test
    void register_assignsDeveloperRole_byDefault() {
        RegisterForm form = new RegisterForm();
        form.setName("Anders And");
        form.setEmail("anders@example.com");
        form.setPassword("password123");

        when(userRepository.emailExists(anyString())).thenReturn(false);

        userService.register(form);

        verify(userRepository).createUser(argThat(user ->
                user.getRole() == Role.DEVELOPER
        ));
    }

    @Test
    void authenticate_returnsUser_whenCredentialsAreCorrect() {
        User gemt = new User();
        gemt.setEmail("anders@example.com");
        gemt.setPasswordHash(passwordEncoder.encode("password123"));

        when(userRepository.findByEmail("anders@example.com")).thenReturn(Optional.of(gemt));

        LoginForm form = new LoginForm();
        form.setEmail("anders@example.com");
        form.setPassword("password123");

        User resultat = userService.authenticate(form);

        assertNotNull(resultat);
        assertEquals("anders@example.com", resultat.getEmail());
    }

    @Test
    void authenticate_returnsNull_whenPasswordIsWrong() {
        User gemt = new User();
        gemt.setEmail("anders@example.com");
        gemt.setPasswordHash(passwordEncoder.encode("rigtig123"));

        when(userRepository.findByEmail("anders@example.com")).thenReturn(Optional.of(gemt));

        LoginForm form = new LoginForm();
        form.setEmail("anders@example.com");
        form.setPassword("forkert123");

        User resultat = userService.authenticate(form);

        assertNull(resultat);
    }

    @Test
    void authenticate_returnsNull_whenUserDoesNotExist() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        LoginForm form = new LoginForm();
        form.setEmail("ingen@example.com");
        form.setPassword("password123");

        User resultat = userService.authenticate(form);

        assertNull(resultat);
    }
}