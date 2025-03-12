package com.werecover.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // 🔹 Prevent infinite recursion: A Sponsee has ONE Sponsor
    @ManyToOne
    @JoinColumn(name = "sponsor_id")
    @JsonBackReference // 👈 Prevents infinite loop in JSON serialization
    private User sponsor;

    // 🔹 A Sponsor has MANY Sponsees
    @OneToMany(mappedBy = "sponsor", cascade = CascadeType.ALL)
    @JsonManagedReference // 👈 Ensures only this side is serialized
    private Set<User> sponsees = new HashSet<>();

    // 🔹 One-to-One Relationship: Each User has ONE Profile
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JsonIgnore // 👈 Prevents profile from being eagerly loaded in JSON response
    private UserProfile profile;

    // Constructors
    public User() {}



    public User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.profile = new UserProfile(this); // ✅ Automatically create profile
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public User getSponsor() { return sponsor; }
    public void setSponsor(User sponsor) { this.sponsor = sponsor; }

    public Set<User> getSponsees() { return sponsees; }
    public void setSponsees(Set<User> sponsees) { this.sponsees = sponsees; }

    public UserProfile getProfile() { return profile; }
    public void setProfile(UserProfile profile) {
        this.profile = profile;
        profile.setUser(this); // ✅ Ensure bi-directional relationship
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", email='" + email + '\'' + ", role=" + role + '}';
    }
}