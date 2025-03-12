package com.werecover.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "achievements")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int milestoneDays; // Number of days required to unlock

    @OneToMany(mappedBy = "achievement", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<UserAchievement> userAchievements; // ✅ Tracks which users earned this achievement

    // Constructors
    public Achievement() {}

    public Achievement(String name, String description, int milestoneDays) {
        this.name = name;
        this.description = description;
        this.milestoneDays = milestoneDays;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getMilestoneDays() { return milestoneDays; }
    public Set<UserAchievement> getUserAchievements() { return userAchievements; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setMilestoneDays(int milestoneDays) { this.milestoneDays = milestoneDays; }
    public void setUserAchievements(Set<UserAchievement> userAchievements) { this.userAchievements = userAchievements; }
}
