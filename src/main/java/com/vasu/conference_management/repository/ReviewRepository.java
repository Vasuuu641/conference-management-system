package com.vasu.conference_management.repository;

import com.vasu.conference_management.entity.Review;
import com.vasu.conference_management.entity.Paper;
import com.vasu.conference_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPaper(Paper paper);
    List<Review> findByReviewer(User reviewer);
    Optional<Review> findByPaperAndReviewer(Paper paper, User reviewer);
    List<Review> findByPaperAndReviewerIsNull(Paper paper);
    long countByPaper(Paper paper);
    double findAverageScoreByPaper(Paper paper);
}

