package com.werecover.backend.service;

import com.werecover.backend.model.*;
import com.werecover.backend.repository.AchievementRepository;
import com.werecover.backend.repository.UserAchievementRepository;
import com.werecover.backend.repository.UserRepository;
import com.werecover.backend.repository.StepWorkRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AchievementService {
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final StepWorkRepository stepWorkRepository;

    public AchievementService(UserRepository userRepository,
                              AchievementRepository achievementRepository,
                              UserAchievementRepository userAchievementRepository,
                              StepWorkRepository stepWorkRepository) {
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.stepWorkRepository = stepWorkRepository;
    }

    /**
     * ✅ Initializes achievements in the database if they don't exist.
     */
    @PostConstruct
    @Transactional
    public void initializeAchievements() {
        if (achievementRepository.count() == 0) { // Only insert if table is empty
            List<Achievement> defaultAchievements = new ArrayList<>(List.of(
                    // 🏆 Sobriety Achievements - Short term
                    new Achievement("First Day", "Completed your first day of sobriety. The journey begins!", 1),
                    new Achievement("High Five", "Five days strong! You're building momentum.", 5),
                    new Achievement("One Week Wonder", "A full week of sobriety. Incredible progress!", 7),
                    new Achievement("Double Digits", "Ten days in recovery. Breaking into double digits!", 10),
                    new Achievement("Two Week Triumph", "Two weeks of commitment to your new path.", 14),
                    new Achievement("Habit Formed", "21 days of sobriety - you're forming new patterns!", 21),
                    new Achievement("Thirty Days Strong", "A full month of recovery. Be proud of yourself!", 30),
                    new Achievement("Sixty Days Sober", "Two months of dedication to your wellbeing.", 60),
                    new Achievement("Ninety Day Milestone", "Three months clean! A significant achievement.", 90),
                    new Achievement("Half Year Hero", "Six months of sobriety. You're an inspiration!", 180),
                    new Achievement("One Year Sober", "A full year in recovery. An amazing accomplishment!", 365),
                    new Achievement("18 Month Milestone", "A year and a half of dedication to your journey.", 547),
                    new Achievement("Two Year Triumph", "Two years sober. Your resilience is remarkable!", 730)
            ));

            // Add yearly achievements from 3-40 years
            for (int years = 3; years <= 40; years++) {
                int days = years * 365;
                String name = years + " Year Milestone";
                String description = years + " years of sobriety. Your commitment is extraordinary!";
                defaultAchievements.add(new Achievement(name, description, days));
            }

            // Add Step Work Achievements
            defaultAchievements.addAll(List.of(
                    new Achievement("Step 1 Completed", "You have completed Step 1!", 1001),
                    new Achievement("Step 2 Completed", "You have completed Step 2!", 1002),
                    new Achievement("Step 3 Completed", "You have completed Step 3!", 1003),
                    new Achievement("Step 4 Completed", "You have completed Step 4!", 1004),
                    new Achievement("Step 5 Completed", "You have completed Step 5!", 1005),
                    new Achievement("Step 6 Completed", "You have completed Step 6!", 1006),
                    new Achievement("Step 7 Completed", "You have completed Step 7!", 1007),
                    new Achievement("Step 8 Completed", "You have completed Step 8!", 1008),
                    new Achievement("Step 9 Completed", "You have completed Step 9!", 1009),
                    new Achievement("Step 10 Completed", "You have completed Step 10!", 1010),
                    new Achievement("Step 11 Completed", "You have completed Step 11!", 1011),
                    new Achievement("Step 12 Completed", "You have completed Step 12!", 1012)
            ));

            achievementRepository.saveAll(defaultAchievements);
            System.out.println("✅ Default achievements populated!");
        }
    }

    /**
     * ✅ Grants sobriety achievements based on the user's sobriety start date.
     */
    public void grantSobrietyAchievements(User user) {
        LocalDate sobrietyStartDate = user.getProfile().getSobrietyStartDate();
        LocalDate today = LocalDate.now();
        long daysSober = ChronoUnit.DAYS.between(sobrietyStartDate, today);

        List<Integer> milestones = List.of(1, 5, 7, 10, 14, 21, 30, 60, 90, 180, 365, 547, 730);
        Set<Integer> existingAchievements = userAchievementRepository.findByUser(user).stream()
                .map(ua -> ua.getAchievement().getMilestoneDays())
                .collect(Collectors.toSet());

        for (Integer milestone : milestones) {
            if (daysSober >= milestone && !existingAchievements.contains(milestone)) {
                Achievement achievement = achievementRepository.findByMilestoneDays(milestone)
                        .orElseThrow(() -> new RuntimeException("Achievement not found for " + milestone + " days"));
                userAchievementRepository.save(new UserAchievement(user, achievement));
                System.out.println("🏆 Achievement granted: " + achievement.getName() + " to " + user.getEmail());
            }
        }
    }

    /**
     * ✅ Grants step work achievements when a user completes a step.
     */
    public void grantStepWorkAchievement(User user, int stepNumber) {
        int milestoneKey = 1000 + stepNumber; // Step 1 → 1001, Step 2 → 1002, etc.

        // Prevent duplicate achievement grants
        if (!userAchievementRepository.existsByUserAndAchievement(user,
                achievementRepository.findByMilestoneDays(milestoneKey).orElse(null))) {

            Achievement achievement = achievementRepository.findByMilestoneDays(milestoneKey)
                    .orElseThrow(() -> new RuntimeException("Achievement not found for Step " + stepNumber));

            userAchievementRepository.save(new UserAchievement(user, achievement));
            System.out.println("🏆 Step Work Achievement granted: " + achievement.getName() + " to " + user.getEmail());
        }
    }

    /**
     * ✅ Runs daily at midnight to grant achievements automatically.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void checkAndGrantAchievements() {
        System.out.println("🔄 Running scheduled achievement check...");
        List<User> users = userRepository.findAll();
        for (User user : users) {
            grantSobrietyAchievements(user);
        }
        System.out.println("✅ Achievement check complete.");
    }

    /**
     * ✅ Fetch all achievements a user has earned.
     */
    public List<UserAchievement> getUserAchievements(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userAchievementRepository.findByUser(user);
    }

    /**
     * ✅ Fetch all available achievements.
     */
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }
}
