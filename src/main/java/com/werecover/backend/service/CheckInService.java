package com.werecover.backend.service;

import com.werecover.backend.model.CheckIn;
import com.werecover.backend.model.User;
import com.werecover.backend.repository.CheckInRepository;
import com.werecover.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckInService {
    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public CheckInService(CheckInRepository checkInRepository, UserRepository userRepository, NotificationService notificationService) {
        this.checkInRepository = checkInRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    /**
     * ✅ Sponsees submit a check-in.
     * Ensures multiple check-ins per day by using timestamps.
     */
    @Transactional
    public CheckIn submitCheckIn(Long sponseeId, boolean sober, boolean struggling, String mood, String notes) {
        User sponsee = userRepository.findById(sponseeId)
                .orElseThrow(() -> new RuntimeException("Sponsee not found"));

        CheckIn checkIn = new CheckIn(sponsee, LocalDateTime.now(), sober, struggling, mood, notes);
        CheckIn savedCheckIn = checkInRepository.save(checkIn);

        // ✅ Notify sponsor if the sponsee is struggling
        if (struggling && sponsee.getSponsor() != null) {
            notificationService.notifySponsorOfStrugglingSponsee(
                    sponsee.getSponsor().getId(),
                    sponsee.getId(),
                    sponsee.getEmail()
            );
        }

        return savedCheckIn;
    }

    /**
     * ✅ Sponsees can view their own check-ins.
     */
    public List<CheckIn> getMyCheckIns(Long sponseeId) {
        return checkInRepository.findByUserIdOrderByTimestampDesc(sponseeId);
    }

    /**
     * ✅ Sponsors can view check-ins of their sponsees.
     */
    public List<CheckIn> getSponseeCheckIns(Long sponsorId) {
        return checkInRepository.findByUser_SponsorIdOrderByTimestampDesc(sponsorId);
    }


}
