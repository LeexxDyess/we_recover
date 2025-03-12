package com.werecover.backend.controller;

import com.werecover.backend.model.CheckIn;
import com.werecover.backend.model.User;
import com.werecover.backend.service.CheckInService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import com.werecover.backend.security.CustomUserDetails;
import com.werecover.backend.repository.UserRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/check-ins")
public class CheckInController {
    private final CheckInService checkInService;
    private final UserRepository userRepository; // ✅ Inject UserRepository

    public CheckInController(CheckInService checkInService, UserRepository userRepository) {
        this.checkInService = checkInService;
        this.userRepository = userRepository; // ✅ Assign it in the constructor
    }

    /**
     * ✅ Sponsees submit a check-in.
     */
    @PostMapping
    public ResponseEntity<CheckIn> submitCheckIn(@RequestBody Map<String, Object> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // ✅ Extract the authenticated user
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId(); // Ensure `CustomUserDetails` has `getId()` method

        // ✅ Extract request parameters
        boolean sober = (boolean) request.get("sober");
        boolean struggling = (boolean) request.get("struggling");
        String mood = (String) request.get("mood");
        String notes = (String) request.get("notes");

        // ✅ Submit the check-in
        CheckIn checkIn = checkInService.submitCheckIn(userId, sober, struggling, mood, notes);
        return ResponseEntity.ok(checkIn);
    }

    /**
     * ✅ Sponsees retrieve their own check-ins.
     */
    @GetMapping("/me")
    public ResponseEntity<List<CheckIn>> getMyCheckIns() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Extract email from authentication
        String userEmail = authentication.getName(); // Spring Security stores email as username
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(checkInService.getMyCheckIns(user.getId()));
    }

    /**
     * ✅ Sponsors retrieve all check-ins of their sponsees.
     */
    @GetMapping("/sponsees")
    public ResponseEntity<List<CheckIn>> getSponseeCheckIns() {
        // 🔹 Get authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 🔹 Extract user details
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long sponsorId = userDetails.getId(); // Ensure `CustomUserDetails` has `getId()` method

        // 🔹 Retrieve check-ins
        List<CheckIn> checkIns = checkInService.getSponseeCheckIns(sponsorId);
        return ResponseEntity.ok(checkIns);
    }
}