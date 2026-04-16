package com.vasu.conference_management.service;

import com.vasu.conference_management.dto.SubmitReviewRequest;
import com.vasu.conference_management.entity.Paper;
import com.vasu.conference_management.entity.Review;
import com.vasu.conference_management.entity.User;
import com.vasu.conference_management.repository.PaperRepository;
import com.vasu.conference_management.repository.ReviewRepository;
import com.vasu.conference_management.repository.UserRepository;
import com.vasu.conference_management.util.ValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final PaperRepository paperRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         PaperRepository paperRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.paperRepository = paperRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Review submitReview(SubmitReviewRequest request) {
        ValidationUtil.requirePositiveId(request.getPaperId(), "paperId");
        ValidationUtil.requirePositiveId(request.getReviewerId(), "reviewerId");

        Paper paper = paperRepository.findById(request.getPaperId())
                .orElseThrow(() -> new IllegalArgumentException("Paper not found: " + request.getPaperId()));

        User reviewer = userRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new IllegalArgumentException("Reviewer not found: " + request.getReviewerId()));

        boolean assigned = paper.getReviewers().stream().anyMatch(r -> r.getId().equals(reviewer.getId()));
        if (!assigned) {
            throw new IllegalStateException("Reviewer is not assigned to this paper");
        }

        reviewRepository.findByPaperAndReviewer(paper, reviewer)
                .ifPresent(existing -> {
                    throw new IllegalStateException("Reviewer has already submitted a review for this paper");
                });

        Review review = new Review();
        review.setPaper(paper);
        review.setReviewer(reviewer);
        review.setScore(request.getScore());
        review.setComments(request.getComments());
        review.setRecommendation(ValidationUtil.normalizeRecommendation(request.getRecommendation()));
        review.setConfidenceLevel(request.getConfidenceLevel());

        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<Review> findByPaper(Long paperId) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new IllegalArgumentException("Paper not found: " + paperId));
        return reviewRepository.findByPaper(paper);
    }

    @Transactional(readOnly = true)
    public Double averageScore(Long paperId) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new IllegalArgumentException("Paper not found: " + paperId));
        return reviewRepository.findAverageScoreByPaper(paper);
    }
}
