package com.vasu.conference_management.controller;

import com.vasu.conference_management.entity.Paper;
import com.vasu.conference_management.service.PaperService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final PaperService paperService;

    public SearchController(PaperService paperService) {
        this.paperService = paperService;
    }

    @GetMapping("/papers")
    public List<Map<String, Object>> searchPapers(@RequestParam(required = false) String title,
                                                  @RequestParam(required = false) Long conferenceId,
                                                  @RequestParam(required = false) Paper.PaperStatus status,
                                                  @RequestParam(required = false) String topic,
                                                  @RequestParam(required = false) Long authorId) {
        return paperService.searchPapers(title, conferenceId, status, topic, authorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Map<String, Object> toResponse(Paper paper) {
        return Map.ofEntries(
                Map.entry("id", paper.getId()),
                Map.entry("title", paper.getTitle()),
                Map.entry("status", paper.getStatus()),
                Map.entry("decision", paper.getDecision() == null ? "PENDING" : paper.getDecision()),
                Map.entry("conferenceId", paper.getConference().getId()),
                Map.entry("conferenceTitle", paper.getConference().getTitle()),
                Map.entry("authorId", paper.getAuthor().getId()),
                Map.entry("authorUsername", paper.getAuthor().getUsername()),
                Map.entry("submissionDate", paper.getSubmissionDate()),
                Map.entry("tags", paper.getTags().stream().map(tag -> tag.getTagName()).toList()),
                Map.entry("reviewerIds", paper.getReviewers().stream().map(reviewer -> reviewer.getId()).toList())
        );
    }
}

