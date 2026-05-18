package com.kodebutikken.pct.service;

import com.kodebutikken.pct.dto.LoginForm;
import com.kodebutikken.pct.dto.RegisterForm;
import com.kodebutikken.pct.model.Role;
import com.kodebutikken.pct.model.User;
import com.kodebutikken.pct.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterForm registerForm) {
        if(userRepository.emailExists(registerForm.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(registerForm.getPassword());

        User user = new User();
        user.setName(registerForm.getName());
        user.setEmail(registerForm.getEmail());
        user.setPasswordHash(hashedPassword);
        user.setRole(Role.DEVELOPER);
        userRepository.createUser(user);
    }

    public User authenticate(LoginForm loginForm) {
        Optional<User> userOptional = userRepository.findByEmail(loginForm.getEmail());
        if (userOptional.isEmpty()) return null;
        User user = userOptional.get();
        if (passwordEncoder.matches(loginForm.getPassword(), user.getPasswordHash())) return user;
        return null;
    }

    public User getUserById(int userId) {
        return userRepository.findById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
