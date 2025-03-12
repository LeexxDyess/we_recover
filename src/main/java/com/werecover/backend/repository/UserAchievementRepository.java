package com.werecover.backend.repository;

import com.werecover.backend.model.User;
import com.werecover.backend.model.Achievement;
import com.werecover.backend.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.List;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    List<UserAchievement> findByUser(User user); // ✅ Find achievements a user already has

    boolean existsByUserAndAchievement(User user, Achievement achievement); // ✅ Check if a user already has an achievement
}
