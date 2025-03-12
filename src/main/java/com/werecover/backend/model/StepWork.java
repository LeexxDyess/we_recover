package com.werecover.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "step_work")
public class StepWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sponsor_id", nullable = false)
    private User sponsor;

    @ManyToOne
    @JoinColumn(name = "sponsee_id", nullable = false)
    private User sponsee;

    private int stepNumber;
    private String instructions;

    @Column(columnDefinition = "TEXT")
    private String response;

    @Enumerated(EnumType.STRING)
    private StepWorkStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ Default Constructor (Required by JPA)
    public StepWork() {}

    // ✅ Constructor With All Fields
    public StepWork(User sponsor, User sponsee, int stepNumber, String instructions, StepWorkStatus status) {
        this.sponsor = sponsor;
        this.sponsee = sponsee;
        this.stepNumber = stepNumber;
        this.instructions = instructions;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ✅ Getters
    public Long getId() {
        return id;
    }

    public User getSponsor() {
        return sponsor;
    }

    public User getSponsee() {
        return sponsee;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getResponse() {
        return response;
    }

    public StepWorkStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ✅ Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setSponsor(User sponsor) {
        this.sponsor = sponsor;
    }

    public void setSponsee(User sponsee) {
        this.sponsee = sponsee;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setStatus(StepWorkStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}