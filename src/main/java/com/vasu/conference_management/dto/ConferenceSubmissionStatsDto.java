package com.vasu.conference_management.dto;

public class ConferenceSubmissionStatsDto {
    private Long conferenceId;
    private String conferenceTitle;
    private long totalSubmissions;
    private long submittedCount;
    private long underReviewCount;
    private long acceptedCount;
    private long rejectedCount;
    private double acceptanceRate;

    public Long getConferenceId() { return conferenceId; }
    public void setConferenceId(Long conferenceId) { this.conferenceId = conferenceId; }
    public String getConferenceTitle() { return conferenceTitle; }
    public void setConferenceTitle(String conferenceTitle) { this.conferenceTitle = conferenceTitle; }
    public long getTotalSubmissions() { return totalSubmissions; }
    public void setTotalSubmissions(long totalSubmissions) { this.totalSubmissions = totalSubmissions; }
    public long getSubmittedCount() { return submittedCount; }
    public void setSubmittedCount(long submittedCount) { this.submittedCount = submittedCount; }
    public long getUnderReviewCount() { return underReviewCount; }
    public void setUnderReviewCount(long underReviewCount) { this.underReviewCount = underReviewCount; }
    public long getAcceptedCount() { return acceptedCount; }
    public void setAcceptedCount(long acceptedCount) { this.acceptedCount = acceptedCount; }
    public long getRejectedCount() { return rejectedCount; }
    public void setRejectedCount(long rejectedCount) { this.rejectedCount = rejectedCount; }
    public double getAcceptanceRate() { return acceptanceRate; }
    public void setAcceptanceRate(double acceptanceRate) { this.acceptanceRate = acceptanceRate; }
}

