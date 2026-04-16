package com.vasu.conference_management.controller;

import com.vasu.conference_management.dto.ConferenceSubmissionStatsDto;
import com.vasu.conference_management.dto.DashboardStatsDto;
import com.vasu.conference_management.dto.UserStatsDto;
import com.vasu.conference_management.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DashboardControllerTests {

    private AutoCloseable mocks;

    private MockMvc mockMvc;

    @Mock
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new DashboardController(dashboardService)).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void overviewReturnsDashboardStats() throws Exception {
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalConferences(3);
        stats.setTotalPapers(10);
        stats.setAcceptedPapers(4);
        stats.setRejectedPapers(2);
        stats.setUnderReviewPapers(1);
        stats.setAcceptanceRate(40.0);

        UserStatsDto userStats = new UserStatsDto();
        userStats.setTotalUsers(12);
        userStats.setAuthorCount(6);
        userStats.setReviewerCount(4);
        userStats.setAdminCount(2);
        stats.setUserStats(userStats);

        ConferenceSubmissionStatsDto conferenceStats = new ConferenceSubmissionStatsDto();
        conferenceStats.setConferenceId(99L);
        conferenceStats.setConferenceTitle("Sample Conference");
        conferenceStats.setTotalSubmissions(5);
        conferenceStats.setSubmittedCount(2);
        conferenceStats.setUnderReviewCount(1);
        conferenceStats.setAcceptedCount(1);
        conferenceStats.setRejectedCount(1);
        conferenceStats.setAcceptanceRate(20.0);
        stats.setConferenceStats(List.of(conferenceStats));

        when(dashboardService.getDashboardStats()).thenReturn(stats);

        mockMvc.perform(get("/api/dashboard").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalConferences").value(3))
                .andExpect(jsonPath("$.totalPapers").value(10))
                .andExpect(jsonPath("$.acceptedPapers").value(4))
                .andExpect(jsonPath("$.rejectedPapers").value(2))
                .andExpect(jsonPath("$.underReviewPapers").value(1))
                .andExpect(jsonPath("$.acceptanceRate").value(40.0))
                .andExpect(jsonPath("$.userStats.totalUsers").value(12))
                .andExpect(jsonPath("$.conferenceStats[0].conferenceTitle").value("Sample Conference"));
    }

    @Test
    void conferenceStatsReturnsPerConferenceList() throws Exception {
        ConferenceSubmissionStatsDto conferenceStats = new ConferenceSubmissionStatsDto();
        conferenceStats.setConferenceId(42L);
        conferenceStats.setConferenceTitle("AI Conference");
        conferenceStats.setTotalSubmissions(8);
        conferenceStats.setSubmittedCount(3);
        conferenceStats.setUnderReviewCount(2);
        conferenceStats.setAcceptedCount(2);
        conferenceStats.setRejectedCount(1);
        conferenceStats.setAcceptanceRate(25.0);

        when(dashboardService.getConferenceStats()).thenReturn(List.of(conferenceStats));

        mockMvc.perform(get("/api/dashboard/conferences").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].conferenceId").value(42L))
                .andExpect(jsonPath("$[0].conferenceTitle").value("AI Conference"))
                .andExpect(jsonPath("$[0].acceptanceRate").value(25.0));
    }
}



