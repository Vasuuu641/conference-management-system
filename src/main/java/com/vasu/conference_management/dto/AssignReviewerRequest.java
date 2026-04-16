package com.vasu.conference_management.dto;

import jakarta.validation.constraints.NotNull;

public class AssignReviewerRequest {
    @NotNull
    private Long paperId;
    @NotNull
    private Long reviewerId;

    public Long getPaperId() { return paperId; }
    public void setPaperId(Long paperId) { this.paperId = paperId; }
    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }
}
