package com.werecover.backend.service;

import com.werecover.backend.model.Role;
import com.werecover.backend.model.User;
import com.werecover.backend.repository.UserRepository;
import com.werecover.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String register(String email, String password, Role role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User(email, passwordEncoder.encode(password), role);
        userRepository.save(user);

        // ✅ Generate a JWT token that includes the user's role
        return jwtService.generateToken(email, role.name());
    }

    public String authenticate(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            // ✅ Generate JWT token including role upon login
            return jwtService.generateToken(user.get().getEmail(), user.get().getRole().name());
        }
        throw new RuntimeException("Invalid credentials");
    }
}
