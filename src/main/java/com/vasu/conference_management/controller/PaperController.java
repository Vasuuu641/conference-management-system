package com.vasu.conference_management.controller;

import com.vasu.conference_management.dto.AssignReviewerRequest;
import com.vasu.conference_management.dto.SubmitPaperRequest;
import com.vasu.conference_management.entity.Paper;
import com.vasu.conference_management.service.PaperService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/papers")
public class PaperController {
    private final PaperService paperService;

    public PaperController(PaperService paperService) {
        this.paperService = paperService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> submit(@Valid @RequestBody SubmitPaperRequest request) {
        return toResponse(paperService.submitPaper(request));
    }

    @PostMapping("/assign-reviewer")
    public Map<String, Object> assignReviewer(@Valid @RequestBody AssignReviewerRequest request) {
        return toResponse(paperService.assignReviewer(request));
    }

    @PatchMapping("/{paperId}/decision")
    public Map<String, Object> updateDecision(@PathVariable Long paperId, @RequestParam Paper.PaperStatus status) {
        return toResponse(paperService.updateDecision(paperId, status));
    }

    @GetMapping
    public List<Map<String, Object>> listAll(@RequestParam(required = false) Long conferenceId,
                                             @RequestParam(required = false) Paper.PaperStatus status) {
        List<Paper> papers;
        if (conferenceId != null) {
            papers = paperService.findByConference(conferenceId);
        } else if (status != null) {
            papers = paperService.findByStatus(status);
        } else {
            papers = paperService.findAll();
        }
        return papers.stream().map(this::toResponse).toList();
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
