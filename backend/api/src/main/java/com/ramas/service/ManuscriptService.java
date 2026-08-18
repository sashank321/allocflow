package com.ramas.service;

import com.ramas.dto.ManuscriptDtos.*;
import com.ramas.entity.Conference;
import com.ramas.entity.ConferenceTrack;
import com.ramas.entity.Manuscript;
import com.ramas.entity.User;
import com.ramas.enums.AuditAction;
import com.ramas.enums.ManuscriptStatus;
import com.ramas.exception.ResourceNotFoundException;
import com.ramas.repository.ConferenceRepository;
import com.ramas.repository.ConferenceTrackRepository;
import com.ramas.repository.ManuscriptRepository;
import com.ramas.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ManuscriptService {

    private final ManuscriptRepository manuscriptRepository;
    private final ConferenceRepository conferenceRepository;
    private final ConferenceTrackRepository trackRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public ManuscriptService(ManuscriptRepository manuscriptRepository,
                             ConferenceRepository conferenceRepository,
                             ConferenceTrackRepository trackRepository,
                             UserRepository userRepository,
                             AuditService auditService) {
        this.manuscriptRepository = manuscriptRepository;
        this.conferenceRepository = conferenceRepository;
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional
    public ManuscriptDto createManuscript(ManuscriptRequest request, String authorEmail, String ipAddress) {
        Conference conference = conferenceRepository.findById(request.conferenceId())
                .orElseThrow(() -> new ResourceNotFoundException("Conference not found with id: " + request.conferenceId()));

        User author = userRepository.findByEmailIgnoreCase(authorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Author user not found"));

        ConferenceTrack track = null;
        if (request.trackId() != null) {
            track = trackRepository.findById(request.trackId()).orElse(null);
        }

        int reqReviews = request.requiredReviews() > 0 ? request.requiredReviews() : conference.getRequiredReviewsPerPaper();

        Manuscript manuscript = new Manuscript(
                conference,
                track,
                author,
                request.title().trim(),
                request.abstractText(),
                reqReviews
        );

        if (request.topics() != null) manuscript.setTopics(request.topics());
        if (request.keywords() != null) manuscript.setKeywords(request.keywords());
        if (request.authorAffiliations() != null) {
            manuscript.setAuthorAffiliations(request.authorAffiliations());
        } else if (author.getAffiliation() != null) {
            manuscript.getAuthorAffiliations().add(author.getAffiliation());
        }

        Manuscript saved = manuscriptRepository.save(manuscript);
        auditService.log(authorEmail, AuditAction.SUBMISSION_CREATED, "MANUSCRIPT", saved.getId().toString(), "Created manuscript: " + saved.getTitle(), ipAddress);

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ManuscriptDto> listManuscripts(UUID conferenceId) {
        if (conferenceId != null) {
            Conference conference = conferenceRepository.findById(conferenceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Conference not found"));
            return manuscriptRepository.findByConference(conference).stream().map(this::toDto).toList();
        }
        return manuscriptRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ManuscriptDto getManuscript(UUID id) {
        Manuscript m = manuscriptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manuscript not found with id: " + id));
        return toDto(m);
    }

    @Transactional
    public ManuscriptDto updateStatus(UUID id, ManuscriptStatus newStatus, String actorEmail, String ipAddress) {
        Manuscript m = manuscriptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manuscript not found with id: " + id));
        m.setStatus(newStatus);
        Manuscript updated = manuscriptRepository.save(m);
        auditService.log(actorEmail, AuditAction.SUBMISSION_UPDATED, "MANUSCRIPT", id.toString(), "Status changed to " + newStatus, ipAddress);
        return toDto(updated);
    }

    public ManuscriptDto toDto(Manuscript m) {
        return new ManuscriptDto(
                m.getId(),
                m.getConference().getId(),
                m.getConference().getCode(),
                m.getTrack() != null ? m.getTrack().getId() : null,
                m.getTrack() != null ? m.getTrack().getName() : null,
                m.getAuthor().getId(),
                m.getAuthor().getFullName(),
                m.getAuthor().getEmail(),
                m.getTitle(),
                m.getAbstractText(),
                m.getStatus(),
                m.getRequiredReviews(),
                m.getTopics(),
                m.getKeywords(),
                m.getAuthorAffiliations(),
                m.getCreatedAt(),
                m.getUpdatedAt()
        );
    }
}
