package com.vasu.conference_management.controller;

import com.vasu.conference_management.dto.CreateConferenceRequest;
import com.vasu.conference_management.entity.Conference;
import com.vasu.conference_management.service.ConferenceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conferences")
public class ConferenceController {
    private final ConferenceService conferenceService;

    public ConferenceController(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@Valid @RequestBody CreateConferenceRequest request) {
        return toResponse(conferenceService.createConference(request));
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return conferenceService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        return toResponse(conferenceService.findById(id));
    }

    private Map<String, Object> toResponse(Conference conference) {
        return Map.of(
                "id", conference.getId(),
                "title", conference.getTitle(),
                "description", conference.getDescription() == null ? "" : conference.getDescription(),
                "startDate", conference.getStartDate(),
                "endDate", conference.getEndDate(),
                "submissionDeadline", conference.getSubmissionDeadline(),
                "status", conference.getStatus(),
                "location", conference.getLocation() == null ? "" : conference.getLocation()
        );
    }
}
