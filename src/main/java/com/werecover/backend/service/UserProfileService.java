package com.werecover.backend.service;

import com.werecover.backend.model.User;
import com.werecover.backend.model.UserProfile;
import com.werecover.backend.repository.UserProfileRepository;
import com.werecover.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final AchievementService achievementService; // ✅ Inject AchievementService

    public UserProfileService(UserProfileRepository userProfileRepository,
                              UserRepository userRepository,
                              AchievementService achievementService) { // ✅ Add AchievementService
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.achievementService = achievementService;
    }

    /**
     * ✅ Retrieves the profile of the currently authenticated user.
     */
    public UserProfile getAuthenticatedUserProfile() {
        User authenticatedUser = getAuthenticatedUser();
        return userProfileRepository.findByUserId(authenticatedUser.getId())
                .orElseThrow(() -> new RuntimeException("User profile not found"));
    }

    /**
     * ✅ Allows users to update their profile and grants achievements if the sobriety start date changes.
     */
    @Transactional
    public UserProfile updateProfile(Long userId, String fullName, LocalDate sobrietyStartDate, String bio, String profilePictureUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        // ✅ Track old sobriety date to detect changes
        LocalDate oldSobrietyStartDate = userProfile.getSobrietyStartDate();

        // ✅ Only update fields if they are provided
        if (fullName != null) userProfile.setFullName(fullName);
        if (bio != null) userProfile.setBio(bio);
        if (profilePictureUrl != null) userProfile.setProfilePictureUrl(profilePictureUrl);

        if (sobrietyStartDate != null && !sobrietyStartDate.equals(oldSobrietyStartDate)) {
            userProfile.setSobrietyStartDate(sobrietyStartDate);
            userProfileRepository.save(userProfile);

            // ✅ Grant missed achievements IMMEDIATELY
            achievementService.grantSobrietyAchievements(user);
        }

        return userProfileRepository.save(userProfile);
    }

    /**
     * ✅ Calculates the number of days a user has been sober.
     */
    public long getSobrietyCounter(Long userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        LocalDate sobrietyStartDate = userProfile.getSobrietyStartDate();
        if (sobrietyStartDate == null) {
            throw new RuntimeException("Sobriety start date is not set.");
        }

        return ChronoUnit.DAYS.between(sobrietyStartDate, LocalDate.now());
    }

    /**
     * ✅ Gets the currently authenticated user from SecurityContext.
     */
    private User getAuthenticatedUser() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
