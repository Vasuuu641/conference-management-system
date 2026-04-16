package com.vasu.conference_management.dto;

public class UserStatsDto {
    private long totalUsers;
    private long authorCount;
    private long reviewerCount;
    private long adminCount;

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getAuthorCount() { return authorCount; }
    public void setAuthorCount(long authorCount) { this.authorCount = authorCount; }
    public long getReviewerCount() { return reviewerCount; }
    public void setReviewerCount(long reviewerCount) { this.reviewerCount = reviewerCount; }
    public long getAdminCount() { return adminCount; }
    public void setAdminCount(long adminCount) { this.adminCount = adminCount; }
}

