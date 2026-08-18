package com.ramas.service;

import com.ramas.dto.ConferenceDtos.*;
import com.ramas.entity.Conference;
import com.ramas.entity.ConferenceTrack;
import com.ramas.exception.BadRequestException;
import com.ramas.exception.ResourceNotFoundException;
import com.ramas.repository.ConferenceRepository;
import com.ramas.repository.ConferenceTrackRepository;
import com.ramas.repository.ManuscriptRepository;
import com.ramas.repository.ReviewerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ConferenceService {

    private final ConferenceRepository conferenceRepository;
    private final ConferenceTrackRepository conferenceTrackRepository;
    private final ManuscriptRepository manuscriptRepository;
    private final ReviewerRepository reviewerRepository;

    public ConferenceService(ConferenceRepository conferenceRepository,
                             ConferenceTrackRepository conferenceTrackRepository,
                             ManuscriptRepository manuscriptRepository,
                             ReviewerRepository reviewerRepository) {
        this.conferenceRepository = conferenceRepository;
        this.conferenceTrackRepository = conferenceTrackRepository;
        this.manuscriptRepository = manuscriptRepository;
        this.reviewerRepository = reviewerRepository;
    }

    @Transactional
    public ConferenceDto createConference(ConferenceRequest request) {
        if (conferenceRepository.existsByCodeIgnoreCase(request.code())) {
            throw new BadRequestException("Conference with code '" + request.code() + "' already exists");
        }

        Conference conference = new Conference(
                request.code().trim().toUpperCase(),
                request.name().trim(),
                request.acronym(),
                request.description(),
                request.requiredReviewsPerPaper() > 0 ? request.requiredReviewsPerPaper() : 2,
                request.defaultReviewerCapacity() > 0 ? request.defaultReviewerCapacity() : 4
        );
        conference.setSubmissionDeadline(request.submissionDeadline());
        conference.setReviewDeadline(request.reviewDeadline());

        Conference saved = conferenceRepository.save(conference);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ConferenceDto> listConferences() {
        return conferenceRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConferenceDto getConference(UUID id) {
        Conference conference = conferenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conference not found with id: " + id));
        return toDto(conference);
    }

    @Transactional(readOnly = true)
    public Conference getEntity(UUID id) {
        return conferenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conference not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<TrackDto> getTracks(UUID conferenceId) {
        Conference conference = getEntity(conferenceId);
        return conferenceTrackRepository.findByConference(conference).stream()
                .map(t -> new TrackDto(t.getId(), conferenceId, t.getName(), t.getDescription()))
                .toList();
    }

    private ConferenceDto toDto(Conference c) {
        long mCount = manuscriptRepository.countByConference(c);
        long rCount = reviewerRepository.countByConference(c);
        return new ConferenceDto(
                c.getId(),
                c.getCode(),
                c.getName(),
                c.getAcronym(),
                c.getDescription(),
                c.getSubmissionDeadline(),
                c.getReviewDeadline(),
                c.getRequiredReviewsPerPaper(),
                c.getDefaultReviewerCapacity(),
                c.getStatus(),
                mCount,
                rCount,
                c.getCreatedAt()
        );
    }
}
