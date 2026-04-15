package com.vasu.conference_management.service;

import com.vasu.conference_management.dto.CreateConferenceRequest;
import com.vasu.conference_management.entity.Conference;
import com.vasu.conference_management.repository.ConferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConferenceService {
    private final ConferenceRepository conferenceRepository;

    public ConferenceService(ConferenceRepository conferenceRepository) {
        this.conferenceRepository = conferenceRepository;
    }

    @Transactional
    public Conference createConference(CreateConferenceRequest request) {
        Conference conference = new Conference();
        conference.setTitle(request.getTitle());
        conference.setDescription(request.getDescription());
        conference.setStartDate(request.getStartDate());
        conference.setEndDate(request.getEndDate());
        conference.setSubmissionDeadline(request.getSubmissionDeadline());
        conference.setNotificationDate(request.getNotificationDate());
        conference.setLocation(request.getLocation());
        conference.setStatus(Conference.ConferenceStatus.OPEN);
        return conferenceRepository.save(conference);
    }

    @Transactional(readOnly = true)
    public List<Conference> findAll() {
        return conferenceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Conference findById(Long id) {
        return conferenceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conference not found: " + id));
    }
}
