package com.ramas.service;

import com.ramas.dto.DashboardStatsDto;
import com.ramas.entity.Assignment;
import com.ramas.entity.Conference;
import com.ramas.entity.Manuscript;
import com.ramas.entity.Reviewer;
import com.ramas.exception.ResourceNotFoundException;
import com.ramas.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AnalyticsService {

    private final ConferenceRepository conferenceRepository;
    private final ManuscriptRepository manuscriptRepository;
    private final ReviewerRepository reviewerRepository;
    private final AssignmentRepository assignmentRepository;
    private final ConflictOfInterestRepository conflictRepository;

    public AnalyticsService(ConferenceRepository conferenceRepository,
                            ManuscriptRepository manuscriptRepository,
                            ReviewerRepository reviewerRepository,
                            AssignmentRepository assignmentRepository,
                            ConflictOfInterestRepository conflictRepository) {
        this.conferenceRepository = conferenceRepository;
        this.manuscriptRepository = manuscriptRepository;
        this.reviewerRepository = reviewerRepository;
        this.assignmentRepository = assignmentRepository;
        this.conflictRepository = conflictRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats(UUID conferenceId) {
        Conference conference = null;
        if (conferenceId != null) {
            conference = conferenceRepository.findById(conferenceId).orElse(null);
        }
        if (conference == null) {
            List<Conference> allConfs = conferenceRepository.findAll();
            if (!allConfs.isEmpty()) {
                conference = allConfs.get(0);
            }
        }

        long totalConfs = conferenceRepository.count();

        if (conference == null) {
            return new DashboardStatsDto(
                    null, "No Active Conference", "NONE",
                    totalConfs, 0, 0, 0, 0, 0.0, 0, 0, 0,
                    Collections.emptyMap(), Collections.emptyMap()
            );
        }

        List<Manuscript> manuscripts = manuscriptRepository.findByConference(conference);
        List<Reviewer> reviewers = reviewerRepository.findByConference(conference);
        List<Assignment> assignments = assignmentRepository.findByConference(conference);
        long conflictCount = conflictRepository.findByConference(conference).size();

        Map<String, Long> statusMap = new HashMap<>();
        int totalRequired = 0;
        for (Manuscript m : manuscripts) {
            statusMap.put(m.getStatus().name(), statusMap.getOrDefault(m.getStatus().name(), 0L) + 1);
            totalRequired += m.getRequiredReviews();
        }

        int activeReviewers = 0;
        int totalCapacity = 0;
        for (Reviewer r : reviewers) {
            if (r.isActive() && r.isAvailable()) {
                activeReviewers++;
                totalCapacity += r.getMaxCapacity();
            }
        }

        Map<String, Integer> workloadMap = new LinkedHashMap<>();
        for (Reviewer r : reviewers) {
            workloadMap.put(r.getUser().getFullName(), 0);
        }
        for (Assignment a : assignments) {
            String name = a.getReviewer().getUser().getFullName();
            workloadMap.put(name, workloadMap.getOrDefault(name, 0) + 1);
        }

        double coverage = totalRequired > 0
                ? Math.min(100.0, ((double) assignments.size() / totalRequired) * 100.0)
                : 0.0;

        return new DashboardStatsDto(
                conference.getId(),
                conference.getName(),
                conference.getCode(),
                totalConfs,
                manuscripts.size(),
                reviewers.size(),
                assignments.size(),
                conflictCount,
                coverage,
                activeReviewers,
                totalCapacity,
                totalRequired,
                statusMap,
                workloadMap
        );
    }
}
