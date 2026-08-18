package com.ramas.service;

import com.ramas.dto.ReviewerDtos.*;
import com.ramas.entity.Assignment;
import com.ramas.entity.Conference;
import com.ramas.entity.Reviewer;
import com.ramas.entity.User;
import com.ramas.enums.AuditAction;
import com.ramas.exception.BadRequestException;
import com.ramas.exception.ResourceNotFoundException;
import com.ramas.repository.AssignmentRepository;
import com.ramas.repository.ConferenceRepository;
import com.ramas.repository.ReviewerRepository;
import com.ramas.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewerService {

    private final ReviewerRepository reviewerRepository;
    private final ConferenceRepository conferenceRepository;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final AuditService auditService;

    public ReviewerService(ReviewerRepository reviewerRepository,
                           ConferenceRepository conferenceRepository,
                           UserRepository userRepository,
                           AssignmentRepository assignmentRepository,
                           AuditService auditService) {
        this.reviewerRepository = reviewerRepository;
        this.conferenceRepository = conferenceRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.auditService = auditService;
    }

    @Transactional
    public ReviewerDto createReviewer(ReviewerRequest request, String actorEmail, String ipAddress) {
        Conference conference = conferenceRepository.findById(request.conferenceId())
                .orElseThrow(() -> new ResourceNotFoundException("Conference not found with id: " + request.conferenceId()));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.userId()));

        if (reviewerRepository.findByUserIdAndConferenceId(user.getId(), conference.getId()).isPresent()) {
            throw new BadRequestException("User is already registered as a reviewer for this conference");
        }

        int capacity = request.maxCapacity() > 0 ? request.maxCapacity() : conference.getDefaultReviewerCapacity();
        Reviewer reviewer = new Reviewer(
                user,
                conference,
                request.affiliation() != null ? request.affiliation() : user.getAffiliation(),
                capacity
        );
        reviewer.setActive(request.active());
        reviewer.setAvailable(request.available());
        if (request.topics() != null) reviewer.setTopics(request.topics());
        if (request.keywords() != null) reviewer.setKeywords(request.keywords());

        Reviewer saved = reviewerRepository.save(reviewer);
        auditService.log(actorEmail, AuditAction.REVIEWER_CREATED, "REVIEWER", saved.getId().toString(), "Added reviewer " + user.getFullName(), ipAddress);

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ReviewerDto> listReviewers(UUID conferenceId) {
        if (conferenceId != null) {
            Conference conference = conferenceRepository.findById(conferenceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Conference not found"));
            return reviewerRepository.findByConference(conference).stream().map(this::toDto).toList();
        }
        return reviewerRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ReviewerDto getReviewer(UUID id) {
        Reviewer r = reviewerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found with id: " + id));
        return toDto(r);
    }

    public ReviewerDto toDto(Reviewer r) {
        List<Assignment> assigned = assignmentRepository.findByReviewerId(r.getId());
        return new ReviewerDto(
                r.getId(),
                r.getUser().getId(),
                r.getUser().getFullName(),
                r.getUser().getEmail(),
                r.getConference().getId(),
                r.getConference().getCode(),
                r.getAffiliation(),
                r.getMaxCapacity(),
                assigned.size(),
                r.isActive(),
                r.isAvailable(),
                r.getTopics(),
                r.getKeywords(),
                r.getCreatedAt()
        );
    }
}
