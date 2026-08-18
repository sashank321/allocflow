package com.ramas.dto;

import java.util.Map;
import java.util.UUID;

public record DashboardStatsDto(
        UUID activeConferenceId,
        String activeConferenceName,
        String activeConferenceCode,
        long totalConferences,
        long totalManuscripts,
        long totalReviewers,
        long totalAssignments,
        long totalConflicts,
        double averageCoveragePercentage,
        int activeReviewersCount,
        int totalReviewerCapacity,
        int totalRequiredReviews,
        Map<String, Long> manuscriptsByStatus,
        Map<String, Integer> reviewerWorkloadDistribution
) {}
