package com.werecover.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_achievements")
public class UserAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    @Column(nullable = false)
    private LocalDateTime earnedDate;

    // Constructors
    public UserAchievement() {}

    public UserAchievement(User user, Achievement achievement) {
        this.user = user;
        this.achievement = achievement;
        this.earnedDate = LocalDateTime.now(); // Set earned date automatically
    }

    // Getters and Setters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Achievement getAchievement() { return achievement; }
    public LocalDateTime getEarnedDate() { return earnedDate; }

    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setAchievement(Achievement achievement) { this.achievement = achievement; }
    public void setEarnedDate(LocalDateTime earnedDate) { this.earnedDate = earnedDate; }
}
