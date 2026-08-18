package com.ramas.service;

import com.ramas.dto.ConflictDtos.*;
import com.ramas.entity.Conference;
import com.ramas.entity.ConflictOfInterest;
import com.ramas.entity.Manuscript;
import com.ramas.entity.Reviewer;
import com.ramas.enums.AuditAction;
import com.ramas.exception.BadRequestException;
import com.ramas.exception.ResourceNotFoundException;
import com.ramas.repository.ConferenceRepository;
import com.ramas.repository.ConflictOfInterestRepository;
import com.ramas.repository.ManuscriptRepository;
import com.ramas.repository.ReviewerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ConflictService {

    private final ConflictOfInterestRepository conflictRepository;
    private final ConferenceRepository conferenceRepository;
    private final ManuscriptRepository manuscriptRepository;
    private final ReviewerRepository reviewerRepository;
    private final AuditService auditService;

    public ConflictService(ConflictOfInterestRepository conflictRepository,
                           ConferenceRepository conferenceRepository,
                           ManuscriptRepository manuscriptRepository,
                           ReviewerRepository reviewerRepository,
                           AuditService auditService) {
        this.conflictRepository = conflictRepository;
        this.conferenceRepository = conferenceRepository;
        this.manuscriptRepository = manuscriptRepository;
        this.reviewerRepository = reviewerRepository;
        this.auditService = auditService;
    }

    @Transactional
    public ConflictDto createConflict(ConflictRequest request, String actorEmail, String ipAddress) {
        Conference conference = conferenceRepository.findById(request.conferenceId())
                .orElseThrow(() -> new ResourceNotFoundException("Conference not found"));

        Manuscript manuscript = manuscriptRepository.findById(request.manuscriptId())
                .orElseThrow(() -> new ResourceNotFoundException("Manuscript not found"));

        Reviewer reviewer = reviewerRepository.findById(request.reviewerId())
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));

        if (conflictRepository.existsByManuscriptIdAndReviewerId(manuscript.getId(), reviewer.getId())) {
            throw new BadRequestException("Conflict already declared for this manuscript-reviewer pair");
        }

        ConflictOfInterest conflict = new ConflictOfInterest(
                conference,
                manuscript,
                reviewer,
                request.conflictType(),
                request.reason()
        );

        ConflictOfInterest saved = conflictRepository.save(conflict);
        auditService.log(actorEmail, AuditAction.CONFLICT_DECLARED, "CONFLICT", saved.getId().toString(),
                String.format("COI (%s) declared between %s and %s", request.conflictType(), manuscript.getTitle(), reviewer.getUser().getFullName()), ipAddress);

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ConflictDto> listConflicts(UUID conferenceId) {
        if (conferenceId != null) {
            Conference conference = conferenceRepository.findById(conferenceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Conference not found"));
            return conflictRepository.findByConference(conference).stream().map(this::toDto).toList();
        }
        return conflictRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public void deleteConflict(UUID id, String actorEmail, String ipAddress) {
        ConflictOfInterest coi = conflictRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conflict not found with id: " + id));
        conflictRepository.delete(coi);
        auditService.log(actorEmail, AuditAction.CONFLICT_DECLARED, "CONFLICT", id.toString(), "COI removed", ipAddress);
    }

    private ConflictDto toDto(ConflictOfInterest c) {
        return new ConflictDto(
                c.getId(),
                c.getConference().getId(),
                c.getManuscript().getId(),
                c.getManuscript().getTitle(),
                c.getReviewer().getId(),
                c.getReviewer().getUser().getFullName(),
                c.getConflictType(),
                c.getReason(),
                c.getCreatedAt()
        );
    }
}
