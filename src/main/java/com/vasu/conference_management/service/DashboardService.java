package com.vasu.conference_management.service;

import com.vasu.conference_management.dto.ConferenceSubmissionStatsDto;
import com.vasu.conference_management.dto.DashboardStatsDto;
import com.vasu.conference_management.dto.UserStatsDto;
import com.vasu.conference_management.entity.Conference;
import com.vasu.conference_management.entity.Paper;
import com.vasu.conference_management.repository.ConferenceRepository;
import com.vasu.conference_management.repository.PaperRepository;
import com.vasu.conference_management.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {
    private final ConferenceRepository conferenceRepository;
    private final PaperRepository paperRepository;
    private final UserRepository userRepository;

    public DashboardService(ConferenceRepository conferenceRepository,
                            PaperRepository paperRepository,
                            UserRepository userRepository) {
        this.conferenceRepository = conferenceRepository;
        this.paperRepository = paperRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        long totalConferences = conferenceRepository.count();
        long totalPapers = paperRepository.count();
        long acceptedPapers = paperRepository.countByStatus(Paper.PaperStatus.ACCEPTED);
        long rejectedPapers = paperRepository.countByStatus(Paper.PaperStatus.REJECTED);
        long underReviewPapers = paperRepository.countByStatus(Paper.PaperStatus.UNDER_REVIEW);

        stats.setTotalConferences(totalConferences);
        stats.setTotalPapers(totalPapers);
        stats.setAcceptedPapers(acceptedPapers);
        stats.setRejectedPapers(rejectedPapers);
        stats.setUnderReviewPapers(underReviewPapers);
        stats.setAcceptanceRate(calculateRate(acceptedPapers, totalPapers));
        stats.setUserStats(buildUserStats());
        stats.setConferenceStats(buildConferenceStats());

        return stats;
    }

    @Transactional(readOnly = true)
    public List<ConferenceSubmissionStatsDto> getConferenceStats() {
        return buildConferenceStats();
    }

    private UserStatsDto buildUserStats() {
        UserStatsDto userStats = new UserStatsDto();
        long totalUsers = userRepository.countAllUsers();
        long authorCount = userRepository.countUsersByRoleName("AUTHOR");
        long reviewerCount = userRepository.countUsersByRoleName("REVIEWER");
        long adminCount = userRepository.countUsersByRoleName("ADMIN");

        userStats.setTotalUsers(totalUsers);
        userStats.setAuthorCount(authorCount);
        userStats.setReviewerCount(reviewerCount);
        userStats.setAdminCount(adminCount);
        return userStats;
    }

    private List<ConferenceSubmissionStatsDto> buildConferenceStats() {
        List<ConferenceSubmissionStatsDto> stats = new ArrayList<>();
        for (Conference conference : conferenceRepository.findAll()) {
            long submitted = paperRepository.countByConference(conference);
            long underReview = paperRepository.countByConferenceAndStatus(conference, Paper.PaperStatus.UNDER_REVIEW);
            long accepted = paperRepository.countByConferenceAndStatus(conference, Paper.PaperStatus.ACCEPTED);
            long rejected = paperRepository.countByConferenceAndStatus(conference, Paper.PaperStatus.REJECTED);
            long total = submitted;

            ConferenceSubmissionStatsDto dto = new ConferenceSubmissionStatsDto();
            dto.setConferenceId(conference.getId());
            dto.setConferenceTitle(conference.getTitle());
            dto.setTotalSubmissions(total);
            dto.setSubmittedCount(total - underReview - accepted - rejected);
            dto.setUnderReviewCount(underReview);
            dto.setAcceptedCount(accepted);
            dto.setRejectedCount(rejected);
            dto.setAcceptanceRate(calculateRate(accepted, total));
            stats.add(dto);
        }
        return stats;
    }

    private double calculateRate(long numerator, long denominator) {
        if (denominator <= 0) {
            return 0.0;
        }
        return Math.round((numerator * 10000.0 / denominator)) / 100.0;
    }
}

