package com.werecover.backend.controller;

import com.werecover.backend.model.Role;
import com.werecover.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        Role role = Role.valueOf(request.get("role").toUpperCase()); // ✅ Ensure role is always uppercase

        String token = authService.register(email, password, role);

        return ResponseEntity.ok(Map.of("token", token, "role", role.name()));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        String token = authService.authenticate(request.get("email"), request.get("password"));

        return ResponseEntity.ok(Map.of("token", token));
    }
}