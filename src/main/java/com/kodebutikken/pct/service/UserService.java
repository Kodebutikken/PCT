package com.kodebutikken.pct.service;

import com.kodebutikken.pct.dto.RegisterForm;
import com.kodebutikken.pct.model.Role;
import com.kodebutikken.pct.model.User;
import com.kodebutikken.pct.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(RegisterForm registerForm) {
        if(userRepository.emailExists(registerForm.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setName(registerForm.getName());
        user.setEmail(registerForm.getEmail());
        user.setPasswordHash(registerForm.getPassword()); // Uden hashing for enkelhed, men bør implementeres i produktion
        user.setRole(Role.DEVELOPER); // Standardrolle, kan ændres baseret på forretningslogik
        userRepository.createUser(user);
    }
}
