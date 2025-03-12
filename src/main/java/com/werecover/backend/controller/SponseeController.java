package com.werecover.backend.controller;

import com.werecover.backend.security.JwtService; // ✅ Ensure this import exists
import com.werecover.backend.service.SponseeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sponsors")
public class SponseeController {
    private final SponseeService sponseeService;
    private final JwtService jwtService;

    public SponseeController(SponseeService sponseeService, JwtService jwtService) {
        this.sponseeService = sponseeService;
        this.jwtService = jwtService;

    }

    @PostMapping("/{sponsorId}/assign-sponsee/{sponseeId}")
    public ResponseEntity<Map<String, String>> assignSponsee(
            @PathVariable Long sponsorId, @PathVariable Long sponseeId,
            @RequestHeader("Authorization") String authHeader) {

        System.out.println("🔍 Received Authorization Header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ No valid Authorization header found.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access Denied"));
        }

        String token = authHeader.replace("Bearer ", "").trim();
        System.out.println("🔍 Extracted Token: " + token);

        // ✅ Call extractRole() as an instance method
        String role = jwtService.extractRole(token);
        System.out.println("🔍 Role Extracted in Controller: " + role);

        if (!"SPONSOR".equals(role)) {
            System.out.println("❌ Unauthorized: User is not a sponsor!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access Denied"));
        }

        String message = sponseeService.assignSponsee(sponsorId, sponseeId);
        return ResponseEntity.ok(Map.of("message", message));
    }
}