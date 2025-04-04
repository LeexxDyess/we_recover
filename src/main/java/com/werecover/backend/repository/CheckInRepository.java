package com.werecover.backend.repository;

import com.werecover.backend.model.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    /**
     * ✅ Find check-ins for a specific sponsee, ordered by most recent.
     */
    List<CheckIn> findByUserIdOrderByTimestampDesc(Long userId);

    /**
     * ✅ Find check-ins for all sponsees of a sponsor, ordered by most recent.
     */
    List<CheckIn> findByUser_SponsorIdOrderByTimestampDesc(Long sponsorId);

    /**
     * ✅ Check if a user has already checked in today (using LocalDateTime)
     */
    boolean existsByUserIdAndTimestampBetween(Long userId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
