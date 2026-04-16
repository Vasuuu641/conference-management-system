package com.vasu.conference_management.repository;

import com.vasu.conference_management.entity.Paper;
import com.vasu.conference_management.entity.Paper.PaperStatus;
import com.vasu.conference_management.entity.Conference;
import com.vasu.conference_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {
    List<Paper> findByConference(Conference conference);
    List<Paper> findByAuthor(User author);
    List<Paper> findByStatus(PaperStatus status);
    List<Paper> findByConferenceAndStatus(Conference conference, PaperStatus status);
    List<Paper> findByTitleContainingIgnoreCase(String title);
    List<Paper> findByAuthorAndConference(User author, Conference conference);
    List<Paper> findByReviewersContaining(User reviewer);
    long countByConference(Conference conference);
    long countByConferenceAndStatus(Conference conference, PaperStatus status);
}

