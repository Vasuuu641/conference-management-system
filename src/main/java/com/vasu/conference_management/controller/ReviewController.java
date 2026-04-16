package com.vasu.conference_management.controller;

import com.vasu.conference_management.dto.SubmitReviewRequest;
import com.vasu.conference_management.entity.Review;
import com.vasu.conference_management.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> submit(@Valid @RequestBody SubmitReviewRequest request) {
        return toResponse(reviewService.submitReview(request));
    }

    @GetMapping
    public List<Map<String, Object>> byPaper(@RequestParam Long paperId) {
        return reviewService.findByPaper(paperId).stream().map(this::toResponse).toList();
    }

    @GetMapping("/average")
    public Map<String, Object> average(@RequestParam Long paperId) {
        Double average = reviewService.averageScore(paperId);
        return Map.of(
                "paperId", paperId,
                "averageScore", average == null ? 0.0 : average
        );
    }

    private Map<String, Object> toResponse(Review review) {
        return Map.of(
                "id", review.getId(),
                "paperId", review.getPaper().getId(),
                "reviewerId", review.getReviewer().getId(),
                "score", review.getScore(),
                "comments", review.getComments(),
                "recommendation", review.getRecommendation() == null ? "" : review.getRecommendation(),
                "confidenceLevel", review.getConfidenceLevel() == null ? 0 : review.getConfidenceLevel(),
                "reviewDate", review.getReviewDate()
        );
    }
}
