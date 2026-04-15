package com.vasu.conference_management.repository;

import com.vasu.conference_management.entity.Conference;
import com.vasu.conference_management.entity.Conference.ConferenceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConferenceRepository extends JpaRepository<Conference, Long> {
    List<Conference> findByStatus(ConferenceStatus status);
    List<Conference> findByStartDateAfter(LocalDateTime date);
    List<Conference> findBySubmissionDeadlineAfter(LocalDateTime date);
    Optional<Conference> findByTitle(String title);
    List<Conference> findByTitleContainingIgnoreCase(String title);
}

