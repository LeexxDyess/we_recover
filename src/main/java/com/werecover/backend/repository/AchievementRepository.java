package com.werecover.backend.repository;

import com.werecover.backend.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    Optional<Achievement> findByMilestoneDays(int milestoneDays);
}
