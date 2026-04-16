package com.vasu.conference_management.service;

import com.vasu.conference_management.dto.ConferenceSubmissionStatsDto;
import com.vasu.conference_management.dto.DashboardStatsDto;
import com.vasu.conference_management.entity.Conference;
import com.vasu.conference_management.entity.Paper;
import com.vasu.conference_management.entity.Tag;
import com.vasu.conference_management.entity.User;
import com.vasu.conference_management.repository.ConferenceRepository;
import com.vasu.conference_management.repository.PaperRepository;
import com.vasu.conference_management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class DashboardServiceTests {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private PaperRepository paperRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    @Test
    void getDashboardStatsAggregatesGlobalAndConferenceMetrics() {
        long baselineConferenceCount = conferenceRepository.count();
        long baselinePaperCount = paperRepository.count();
        long baselineTotalUsers = userRepository.countAllUsers();
        long baselineAuthors = userRepository.countUsersByRoleName("AUTHOR");
        long baselineReviewers = userRepository.countUsersByRoleName("REVIEWER");
        long baselineAdmins = userRepository.countUsersByRoleName("ADMIN");
        long baselineAccepted = paperRepository.countByStatus(Paper.PaperStatus.ACCEPTED);
        long baselineRejected = paperRepository.countByStatus(Paper.PaperStatus.REJECTED);
        long baselineUnderReview = paperRepository.countByStatus(Paper.PaperStatus.UNDER_REVIEW);

        String suffix = String.valueOf(System.nanoTime());

        User author = createUser("dash-author-" + suffix, "dash-author-" + suffix + "@example.com", "AUTHOR");
        User reviewer = createUser("dash-reviewer-" + suffix, "dash-reviewer-" + suffix + "@example.com", "REVIEWER");
        User admin = createUser("dash-admin-" + suffix, "dash-admin-" + suffix + "@example.com", "ADMIN");

        Tag tag = tagService.createTag("DashboardTag-" + suffix, "stats tag");

        Conference conference = new Conference();
        conference.setTitle("Dashboard Conference " + suffix);
        conference.setDescription("Stats test conference");
        conference.setStartDate(LocalDateTime.now().plusMonths(2));
        conference.setEndDate(LocalDateTime.now().plusMonths(2).plusDays(3));
        conference.setSubmissionDeadline(LocalDateTime.now().plusMonths(1));
        conference.setNotificationDate(LocalDateTime.now().plusMonths(1).plusDays(10));
        conference.setLocation("Test City");
        conference.setStatus(Conference.ConferenceStatus.OPEN);
        conference = conferenceRepository.save(conference);
        final Long conferenceId = conference.getId();

        savePaper(conference, author, tag, Paper.PaperStatus.SUBMITTED, "Submitted paper");
        savePaper(conference, author, tag, Paper.PaperStatus.UNDER_REVIEW, "Under review paper");
        savePaper(conference, author, tag, Paper.PaperStatus.ACCEPTED, "Accepted paper");
        savePaper(conference, author, tag, Paper.PaperStatus.REJECTED, "Rejected paper");

        DashboardStatsDto stats = dashboardService.getDashboardStats();
        assertNotNull(stats);
        assertEquals(baselineConferenceCount + 1, stats.getTotalConferences());
        assertEquals(baselinePaperCount + 4, stats.getTotalPapers());
        assertEquals(baselineAccepted + 1, stats.getAcceptedPapers());
        assertEquals(baselineRejected + 1, stats.getRejectedPapers());
        assertEquals(baselineUnderReview + 1, stats.getUnderReviewPapers());
        assertEquals(Math.round(((baselineAccepted + 1) * 10000.0 / (baselinePaperCount + 4))) / 100.0, stats.getAcceptanceRate());

        assertNotNull(stats.getUserStats());
        assertEquals(baselineTotalUsers + 3, stats.getUserStats().getTotalUsers());
        assertEquals(baselineAuthors + 1, stats.getUserStats().getAuthorCount());
        assertEquals(baselineReviewers + 1, stats.getUserStats().getReviewerCount());
        assertEquals(baselineAdmins + 1, stats.getUserStats().getAdminCount());

        ConferenceSubmissionStatsDto conferenceStats = stats.getConferenceStats().stream()
                .filter(dto -> conferenceId.equals(dto.getConferenceId()))
                .findFirst()
                .orElseThrow();

        assertEquals(4, conferenceStats.getTotalSubmissions());
        assertEquals(1, conferenceStats.getSubmittedCount());
        assertEquals(1, conferenceStats.getUnderReviewCount());
        assertEquals(1, conferenceStats.getAcceptedCount());
        assertEquals(1, conferenceStats.getRejectedCount());
        assertEquals(25.0, conferenceStats.getAcceptanceRate());
    }

    @Test
    void getConferenceStatsReturnsStatsForSavedConference() {
        User author = createUser("dash-author-list-" + System.nanoTime(), "dash-author-list@example.com", "AUTHOR");
        Tag tag = tagService.createTag("DashboardTagList-" + System.nanoTime(), "stats tag");

        Conference conference = new Conference();
        conference.setTitle("Dashboard Conference List " + System.nanoTime());
        conference.setDescription("List stats test");
        conference.setStartDate(LocalDateTime.now().plusMonths(2));
        conference.setEndDate(LocalDateTime.now().plusMonths(2).plusDays(2));
        conference.setSubmissionDeadline(LocalDateTime.now().plusMonths(1));
        conference.setNotificationDate(LocalDateTime.now().plusMonths(1).plusDays(5));
        conference.setLocation("Test City");
        conference.setStatus(Conference.ConferenceStatus.OPEN);
        conference = conferenceRepository.save(conference);
        final Long conferenceId = conference.getId();

        savePaper(conference, author, tag, Paper.PaperStatus.ACCEPTED, "Accepted only");

        ConferenceSubmissionStatsDto conferenceStats = dashboardService.getConferenceStats().stream()
                .filter(dto -> conferenceId.equals(dto.getConferenceId()))
                .findFirst()
                .orElseThrow();

        assertEquals(1, conferenceStats.getTotalSubmissions());
        assertEquals(1, conferenceStats.getAcceptedCount());
        assertEquals(100.0, conferenceStats.getAcceptanceRate());
    }

    private User createUser(String username, String email, String roleName) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("secret");
        user.setFirstName("Dash");
        user.setLastName(roleName);
        return userService.register(user, Set.of(roleName));
    }

    private Paper savePaper(Conference conference, User author, Tag tag, Paper.PaperStatus status, String title) {
        Paper paper = new Paper();
        paper.setTitle(title);
        paper.setAbstractText("Abstract for " + title);
        paper.setFilePath("/uploads/" + title.replace(' ', '-').toLowerCase() + ".pdf");
        paper.setKeywords("dashboard,stats");
        paper.setConference(conference);
        paper.setAuthor(author);
        paper.setTags(Set.of(tag));
        paper.setStatus(status);
        paper.setDecision(status == Paper.PaperStatus.ACCEPTED ? "ACCEPTED" : status == Paper.PaperStatus.REJECTED ? "REJECTED" : "PENDING");
        return paperRepository.save(paper);
    }
}


