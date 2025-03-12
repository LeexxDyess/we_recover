package com.werecover.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String fullName;
    private LocalDate sobrietyStartDate;
    private String bio;
    private String profilePictureUrl;

    // Constructors
    public UserProfile() {}

    public UserProfile(User user) {
        this.user = user;
        this.fullName = "";
        this.sobrietyStartDate = null;
        this.bio = "";
        this.profilePictureUrl = "";
    }

    public UserProfile(User user, String fullName, LocalDate sobrietyStartDate, String bio, String profilePictureUrl) {
        this.user = user;
        this.fullName = fullName;
        this.sobrietyStartDate = sobrietyStartDate;
        this.bio = bio;
        this.profilePictureUrl = profilePictureUrl;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getFullName() { return fullName; }
    public LocalDate getSobrietyStartDate() { return sobrietyStartDate; }
    public String getBio() { return bio; }
    public String getProfilePictureUrl() { return profilePictureUrl; }

    public void setUser(User user) { this.user = user; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setSobrietyStartDate(LocalDate sobrietyStartDate) { this.sobrietyStartDate = sobrietyStartDate; }
    public void setBio(String bio) { this.bio = bio; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
}
