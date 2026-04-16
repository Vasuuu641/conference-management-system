package com.vasu.conference_management.dto;

import java.util.ArrayList;
import java.util.List;

public class DashboardStatsDto {
    private long totalConferences;
    private long totalPapers;
    private long acceptedPapers;
    private long rejectedPapers;
    private long underReviewPapers;
    private double acceptanceRate;
    private UserStatsDto userStats;
    private List<ConferenceSubmissionStatsDto> conferenceStats = new ArrayList<>();

    public long getTotalConferences() { return totalConferences; }
    public void setTotalConferences(long totalConferences) { this.totalConferences = totalConferences; }
    public long getTotalPapers() { return totalPapers; }
    public void setTotalPapers(long totalPapers) { this.totalPapers = totalPapers; }
    public long getAcceptedPapers() { return acceptedPapers; }
    public void setAcceptedPapers(long acceptedPapers) { this.acceptedPapers = acceptedPapers; }
    public long getRejectedPapers() { return rejectedPapers; }
    public void setRejectedPapers(long rejectedPapers) { this.rejectedPapers = rejectedPapers; }
    public long getUnderReviewPapers() { return underReviewPapers; }
    public void setUnderReviewPapers(long underReviewPapers) { this.underReviewPapers = underReviewPapers; }
    public double getAcceptanceRate() { return acceptanceRate; }
    public void setAcceptanceRate(double acceptanceRate) { this.acceptanceRate = acceptanceRate; }
    public UserStatsDto getUserStats() { return userStats; }
    public void setUserStats(UserStatsDto userStats) { this.userStats = userStats; }
    public List<ConferenceSubmissionStatsDto> getConferenceStats() { return conferenceStats; }
    public void setConferenceStats(List<ConferenceSubmissionStatsDto> conferenceStats) { this.conferenceStats = conferenceStats; }
}

