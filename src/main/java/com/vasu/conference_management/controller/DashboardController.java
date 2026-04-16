package com.vasu.conference_management.controller;

import com.vasu.conference_management.dto.ConferenceSubmissionStatsDto;
import com.vasu.conference_management.dto.DashboardStatsDto;
import com.vasu.conference_management.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardStatsDto overview() {
        return dashboardService.getDashboardStats();
    }

    @GetMapping("/conferences")
    public List<ConferenceSubmissionStatsDto> conferenceStats() {
        return dashboardService.getConferenceStats();
    }
}

