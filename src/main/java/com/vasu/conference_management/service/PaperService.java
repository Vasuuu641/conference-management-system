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
import com.vasu.conference_management.util.DateUtil;
import com.vasu.conference_management.util.FileUploadUtil;
import com.vasu.conference_management.util.ValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
        ValidationUtil.requirePositiveId(request.getConferenceId(), "conferenceId");
        ValidationUtil.requirePositiveId(request.getAuthorId(), "authorId");
        ValidationUtil.requireNonEmpty(request.getTagIds(), "tagIds");

        Paper paper = new Paper();
        paper.setTitle(request.getTitle());
        paper.setAbstractText(request.getAbstractText());
        paper.setFilePath(FileUploadUtil.validateAndNormalizePaperPath(request.getFilePath()));
        paper.setKeywords(request.getKeywords());
        paper.setStatus(Paper.PaperStatus.SUBMITTED);
        paper.setDecision("PENDING");

        var conference = conferenceRepository.findById(request.getConferenceId())
                .orElseThrow(() -> new IllegalArgumentException("Conference not found: " + request.getConferenceId()));
        if (!DateUtil.isSubmissionWindowOpen(conference.getSubmissionDeadline())) {
            throw new IllegalStateException("Submission deadline has passed for this conference");
        }
        paper.setConference(conference);

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
        ValidationUtil.requirePositiveId(request.getPaperId(), "paperId");
        ValidationUtil.requirePositiveId(request.getReviewerId(), "reviewerId");

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

    @Transactional(readOnly = true)
    public List<Paper> findByAuthor(Long authorId) {
        ValidationUtil.requirePositiveId(authorId, "authorId");
        userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Author not found: " + authorId));
        return paperRepository.findByAuthorId(authorId);
    }

    @Transactional(readOnly = true)
    public List<Paper> searchPapers(String title,
                                    Long conferenceId,
                                    Paper.PaperStatus status,
                                    String topic,
                                    Long authorId) {
        List<Paper> papers = paperRepository.findAll();

        if (conferenceId != null) {
            ValidationUtil.requirePositiveId(conferenceId, "conferenceId");
            conferenceRepository.findById(conferenceId)
                    .orElseThrow(() -> new IllegalArgumentException("Conference not found: " + conferenceId));
            papers = papers.stream()
                    .filter(paper -> conferenceId.equals(paper.getConference().getId()))
                    .toList();
        }

        if (status != null) {
            papers = papers.stream()
                    .filter(paper -> status == paper.getStatus())
                    .toList();
        }

        if (authorId != null) {
            ValidationUtil.requirePositiveId(authorId, "authorId");
            userRepository.findById(authorId)
                    .orElseThrow(() -> new IllegalArgumentException("Author not found: " + authorId));
            papers = papers.stream()
                    .filter(paper -> authorId.equals(paper.getAuthor().getId()))
                    .toList();
        }

        if (title != null && !title.isBlank()) {
            String normalizedTitle = title.trim().toLowerCase(Locale.ROOT);
            papers = papers.stream()
                    .filter(paper -> paper.getTitle() != null
                            && paper.getTitle().toLowerCase(Locale.ROOT).contains(normalizedTitle))
                    .toList();
        }

        if (topic != null && !topic.isBlank()) {
            String normalizedTopic = topic.trim().toLowerCase(Locale.ROOT);
            papers = papers.stream()
                    .filter(paper -> paper.getTags().stream().anyMatch(tag -> tag.getTagName() != null
                            && tag.getTagName().toLowerCase(Locale.ROOT).contains(normalizedTopic)))
                    .toList();
        }

        return papers;
    }
}
