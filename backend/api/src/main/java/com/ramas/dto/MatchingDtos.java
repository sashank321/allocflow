package com.ramas.dto;

import com.ramas.enums.AlgorithmType;
import com.ramas.enums.AssignmentRunStatus;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MatchingDtos {

    public record SimulationRequest(
            @NotNull UUID conferenceId,
            @NotNull AlgorithmType algorithm,
            Integer requiredReviewsPerPaper,
            Integer defaultReviewerCapacity,
            Boolean excludeConflicts
    ) {}

    public record AssignedPairDto(
            UUID manuscriptId,
            String manuscriptTitle,
            UUID reviewerId,
            String reviewerName,
            String reviewerAffiliation,
            long flow,
            double compatibilityScore,
            int topicOverlapCount,
            int keywordOverlapCount
    ) {}

    public record GraphNodeDto(
            String id,
            String label,
            String type, // SOURCE, MANUSCRIPT, REVIEWER, SINK
            int capacity,
            int currentFlow,
            Map<String, Object> metadata
    ) {}

    public record GraphEdgeDto(
            String source,
            String target,
            long capacity,
            long flow,
            boolean saturated,
            String type, // SOURCE_TO_MANUSCRIPT, MANUSCRIPT_TO_REVIEWER, REVIEWER_TO_SINK
            String manuscriptId,
            String reviewerId
    ) {}

    public record GraphVisualizationDto(
            List<GraphNodeDto> nodes,
            List<GraphEdgeDto> edges
    ) {}

    public record ValidationSummaryDto(
            boolean valid,
            int totalAssignedPairs,
            int totalRequiredReviews,
            double coveragePercentage,
            int fullySatisfiedManuscripts,
            int partiallySatisfiedManuscripts,
            int zeroReviewManuscripts,
            List<String> errors,
            List<String> warnings
    ) {}

    public record SimulationResponse(
            UUID runId,
            UUID conferenceId,
            String conferenceCode,
            AlgorithmType algorithm,
            String algorithmName,
            String theoreticalComplexity,
            String graphFingerprint,
            int totalManuscripts,
            int totalReviewers,
            int totalVertices,
            int totalEdges,
            long totalRequiredFlow,
            long achievedFlow,
            double coveragePercentage,
            double durationMs,
            int augmentationsCount,
            int phasesCount,
            AssignmentRunStatus status,
            ValidationSummaryDto validation,
            List<AssignedPairDto> assignments,
            GraphVisualizationDto graphVisualization,
            List<String> executionTraceSummary
    ) {}

    public record CommitRequest(
            String notes
    ) {}

    public record CommitResponse(
            UUID runId,
            AssignmentRunStatus status,
            int committedAssignmentsCount,
            Instant committedAt,
            String committedByEmail
    ) {}

    public record OverrideRequest(
            @NotNull UUID conferenceId,
            @NotNull UUID manuscriptId,
            @NotNull UUID reviewerId,
            String overrideReason
    ) {}

    public record OverrideResponse(
            UUID assignmentId,
            UUID manuscriptId,
            UUID reviewerId,
            boolean success,
            String message
    ) {}
}
