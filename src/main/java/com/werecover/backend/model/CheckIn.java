package com.werecover.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "check_ins")
public class CheckIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime timestamp; // ✅ Stores exact check-in time

    @Column(nullable = false)
    private boolean sober; // ✅ Did they stay sober?

    @Column(nullable = false)
    private boolean struggling; // ✅ Do they need support?

    @Column(length = 255)
    private String mood; // ✅ Optional mood description

    @Column(columnDefinition = "TEXT")
    private String notes; // ✅ Optional notes

    // Constructors
    public CheckIn() {}

    public CheckIn(User user, LocalDateTime timestamp, boolean sober, boolean struggling, String mood, String notes) {
        this.user = user;
        this.timestamp = timestamp;
        this.sober = sober;
        this.struggling = struggling;
        this.mood = mood;
        this.notes = notes;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isSober() { return sober; }
    public boolean isStruggling() { return struggling; }
    public String getMood() { return mood; }
    public String getNotes() { return notes; }

    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setSober(boolean sober) { this.sober = sober; }
    public void setStruggling(boolean struggling) { this.struggling = struggling; }
    public void setMood(String mood) { this.mood = mood; }
    public void setNotes(String notes) { this.notes = notes; }
}
