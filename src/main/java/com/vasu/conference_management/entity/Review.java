package com.vasu.conference_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "score", nullable = false)
    private Integer score; // 1-5 or 1-10 scale

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @CreationTimestamp
    @Column(name = "review_date", nullable = false, updatable = false)
    private LocalDateTime reviewDate;

    @Column(name = "recommendation")
    private String recommendation; // ACCEPT, REJECT, WEAK_ACCEPT, WEAK_REJECT

    // Many-to-One: Reviews -> Paper
    @ManyToOne(optional = false)
    @JoinColumn(name = "paper_id", nullable = false)
    private Paper paper;

    // Many-to-One: Reviews -> User (Reviewer)
    @ManyToOne(optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @Column(name = "confidence_level")
    private Integer confidenceLevel; // 1-5 scale for reviewer confidence
}

