package com.werecover.backend.controller;

import com.werecover.backend.model.User;
import com.werecover.backend.model.UserProfile;
import com.werecover.backend.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import com.werecover.backend.security.CustomUserDetails;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/user-profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * Retrieves the profile of the currently authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfile> getAuthenticatedUserProfile() {
        return ResponseEntity.ok(userProfileService.getAuthenticatedUserProfile()); // ✅ Corrected: No arguments
    }

    /**
     * Allows the authenticated user to update their own profile.
     */
    @PutMapping("/me")
    public ResponseEntity<UserProfile> updateAuthenticatedUserProfile(@RequestBody Map<String, Object> request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId(); // Ensure `CustomUserDetails` has `getId()`

        // ✅ Extract fields safely
        String fullName = (String) request.get("fullName");
        String bio = (String) request.get("bio");
        String profilePictureUrl = (String) request.get("profilePictureUrl");

        // ✅ Fix: Only parse date if it's present
        LocalDate sobrietyStartDate = null;
        if (request.containsKey("sobrietyStartDate") && request.get("sobrietyStartDate") != null) {
            try {
                sobrietyStartDate = LocalDate.parse((String) request.get("sobrietyStartDate"));
            } catch (Exception e) {
                System.out.println("⚠️ Invalid sobrietyStartDate provided: " + request.get("sobrietyStartDate"));
                return ResponseEntity.badRequest().body(null);
            }
        }

        return ResponseEntity.ok(userProfileService.updateProfile(userId, fullName, sobrietyStartDate, bio, profilePictureUrl));
    }

    /**
     * ✅ Retrieves the sobriety counter for the authenticated user.
     */
    @GetMapping("/me/sobriety-counter")
    public ResponseEntity<Long> getSobrietyCounter() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long daysSober = userProfileService.getSobrietyCounter(userDetails.getId());

        return ResponseEntity.ok(daysSober);
    }
}