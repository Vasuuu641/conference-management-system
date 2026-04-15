package com.vasu.conference_management.service;

import com.vasu.conference_management.dto.AssignReviewerRequest;
import com.vasu.conference_management.dto.SubmitPaperRequest;
import com.vasu.conference_management.entity.Paper;
import com.vasu.conference_management.entity.Tag;
import com.vasu.conference_management.entity.User;
import com.vasu.conference_management.repository.ConferenceRepository;
import com.vasu.conference_management.repository.PaperRepository;
import com.vasu.conference_management.repository.TagRepository;
import com.vasu.conference_management.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PaperService {
    private final PaperRepository paperRepository;
    private final ConferenceRepository conferenceRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    public PaperService(PaperRepository paperRepository,
                        ConferenceRepository conferenceRepository,
                        UserRepository userRepository,
                        TagRepository tagRepository) {
        this.paperRepository = paperRepository;
        this.conferenceRepository = conferenceRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Paper submitPaper(SubmitPaperRequest request) {
        Paper paper = new Paper();
        paper.setTitle(request.getTitle());
        paper.setAbstractText(request.getAbstractText());
        paper.setFilePath(request.getFilePath());
        paper.setKeywords(request.getKeywords());
        paper.setStatus(Paper.PaperStatus.SUBMITTED);
        paper.setDecision("PENDING");

        paper.setConference(conferenceRepository.findById(request.getConferenceId())
                .orElseThrow(() -> new IllegalArgumentException("Conference not found: " + request.getConferenceId())));

        paper.setAuthor(userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Author not found: " + request.getAuthorId())));

        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
        if (tags.isEmpty()) {
            throw new IllegalArgumentException("At least one valid tag is required");
        }
        paper.setTags(tags);

        return paperRepository.save(paper);
    }

    @Transactional
    public Paper assignReviewer(AssignReviewerRequest request) {
        Paper paper = paperRepository.findById(request.getPaperId())
                .orElseThrow(() -> new IllegalArgumentException("Paper not found: " + request.getPaperId()));

        User reviewer = userRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new IllegalArgumentException("Reviewer not found: " + request.getReviewerId()));

        paper.getReviewers().add(reviewer);
        paper.setStatus(Paper.PaperStatus.UNDER_REVIEW);
        return paperRepository.save(paper);
    }

    @Transactional
    public Paper updateDecision(Long paperId, Paper.PaperStatus status) {
        if (status != Paper.PaperStatus.ACCEPTED && status != Paper.PaperStatus.REJECTED) {
            throw new IllegalArgumentException("Decision status must be ACCEPTED or REJECTED");
        }

        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new IllegalArgumentException("Paper not found: " + paperId));

        paper.setStatus(status);
        paper.setDecision(status.name());
        return paperRepository.save(paper);
    }

    @Transactional(readOnly = true)
    public List<Paper> findAll() {
        return paperRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Paper> findByConference(Long conferenceId) {
        var conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new IllegalArgumentException("Conference not found: " + conferenceId));
        return paperRepository.findByConference(conference);
    }

    @Transactional(readOnly = true)
    public List<Paper> findByStatus(Paper.PaperStatus status) {
        return paperRepository.findByStatus(status);
    }
}
