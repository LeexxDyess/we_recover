package com.werecover.backend.service;

import com.werecover.backend.model.User;
import com.werecover.backend.repository.CheckInRepository;
import com.werecover.backend.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final UserRepository userRepository;
    private final CheckInRepository checkInRepository;

    public NotificationService(UserRepository userRepository, CheckInRepository checkInRepository) {
        this.userRepository = userRepository;
        this.checkInRepository = checkInRepository;
    }

    /**
     * ✅ Notifies a sponsor when their sponsee is struggling.
     */
    public void notifySponsorOfStrugglingSponsee(Long sponsorId, Long sponseeId, String sponseeEmail) {
        System.out.println("📢 Alert! Sponsor (ID: " + sponsorId + ") – Your sponsee (ID: " + sponseeId + ", Email: " + sponseeEmail + ") is struggling!");
        // TODO: Replace with actual notification logic (email, push notification, etc.)
    }

    /**
     * ✅ Daily reminder for sponsees to check in (Runs every day at 6 PM).
     */
    @Scheduled(cron = "0 0 18 * * ?") // Every day at 6 PM
    public void sendDailyCheckInReminders() {
        System.out.println("🔔 Running daily check-in reminder notifications...");

        List<User> sponsees = userRepository.findByRole("ROLE_SPONSEE"); // Find all sponsees

        for (User sponsee : sponsees) {
            LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);

            boolean hasCheckedInToday = checkInRepository.existsByUserIdAndTimestampBetween(sponsee.getId(), startOfDay, endOfDay);

            if (!hasCheckedInToday) {
                sendReminderToSponsee(sponsee);
            }
        }

        System.out.println("✅ Daily check-in reminder notifications sent.");
    }

    /**
     * ✅ Notifies a sponsee to check in.
     */
    private void sendReminderToSponsee(User sponsee) {
        System.out.println("📢 Reminder: " + sponsee.getEmail() + ", don't forget to check in today!");
        // TODO: Replace with actual notification logic (email, push notification, etc.)
    }

    /**
     * ✅ Notifies sponsors of new check-ins from their sponsees.
     */
    public void notifySponsorOfNewCheckIn(Long sponsorId, Long sponseeId) {
        System.out.println("📢 Sponsor (ID: " + sponsorId + ") – Your sponsee (ID: " + sponseeId + ") has submitted a new check-in!");
        // TODO: Replace with actual notification logic (email, push notification, etc.)
    }
}
