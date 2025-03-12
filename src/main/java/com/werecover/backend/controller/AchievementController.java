package com.werecover.backend.controller;

import com.werecover.backend.model.Achievement;
import com.werecover.backend.model.User;
import com.werecover.backend.model.UserAchievement;
import com.werecover.backend.repository.UserRepository;
import com.werecover.backend.service.AchievementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementService achievementService;
    private final UserRepository userRepository;

    public AchievementController(AchievementService achievementService, UserRepository userRepository) {
        this.achievementService = achievementService;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all achievements the authenticated user has earned.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getUserAchievements() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Grant any new achievements first
            achievementService.grantSobrietyAchievements(user);

            List<UserAchievement> userAchievements = achievementService.getUserAchievements(email);
            List<Achievement> achievements = userAchievements.stream()
                    .map(UserAchievement::getAchievement)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(achievements);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving achievements: " + e.getMessage());
        }
    }

    /**
     * Retrieves all possible achievements (for reference).
     */
    @GetMapping("/all")
    public ResponseEntity<List<Achievement>> getAllAchievements() {
        return ResponseEntity.ok(achievementService.getAllAchievements());
    }
}