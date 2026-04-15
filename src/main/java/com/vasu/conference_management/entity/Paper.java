package com.vasu.conference_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "papers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "abstract_text", columnDefinition = "TEXT")
    private String abstractText;

    @Column(name = "file_path")
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaperStatus status = PaperStatus.SUBMITTED;

    @CreationTimestamp
    @Column(name = "submission_date", nullable = false, updatable = false)
    private LocalDateTime submissionDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "keywords")
    private String keywords;

    // Many-to-One: Papers -> Conference
    @ManyToOne(optional = false)
    @JoinColumn(name = "conference_id", nullable = false)
    private Conference conference;

    // Many-to-One: Papers -> User (Author)
    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // Many-to-Many: Papers -> Tags
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "paper_tags",
            joinColumns = @JoinColumn(name = "paper_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    // Many-to-Many: Papers -> Reviewers
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "paper_reviewers",
            joinColumns = @JoinColumn(name = "paper_id"),
            inverseJoinColumns = @JoinColumn(name = "reviewer_id")
    )
    private Set<User> reviewers = new HashSet<>();

    // One-to-Many: Paper -> Reviews
    @OneToMany(mappedBy = "paper", cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();

    @Column(name = "decision")
    private String decision; // ACCEPTED, REJECTED, PENDING

    public enum PaperStatus {
        SUBMITTED, UNDER_REVIEW, ACCEPTED, REJECTED
    }
}

