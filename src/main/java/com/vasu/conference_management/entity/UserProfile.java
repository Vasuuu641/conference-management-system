package com.vasu.conference_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "affiliation")
    private String affiliation;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "phone")
    private String phone;

    @Column(name = "research_interests")
    private String researchInterests;

    // One-to-One relationship with User
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}

