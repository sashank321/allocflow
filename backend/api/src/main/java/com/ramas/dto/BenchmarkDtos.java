package com.ramas.dto;

import java.util.List;
import java.util.Map;

public class BenchmarkDtos {

    public record BenchmarkRequest(
            int manuscriptCount,
            int reviewerCount,
            Integer requiredReviewsPerPaper,
            Integer reviewerCapacity,
            Double eligibilityProbability,
            Double conflictProbability,
            Integer topicCount,
            Long randomSeed,
            Integer warmupTrials,
            Integer measuredTrials
    ) {}

    public record AlgorithmMetricDto(
            String algorithmName,
            String theoreticalComplexity,
            String graphFingerprint,
            long maxFlow,
            int warmupTrials,
            int measuredTrials,
            double minDurationMs,
            double medianDurationMs,
            double p95DurationMs,
            double maxDurationMs,
            double meanDurationMs,
            double stdDevDurationMs,
            int augmentations,
            int phases,
            String validityStatus,
            boolean invariantVerified
    ) {}

    public record BenchmarkComparisonResponse(
            String datasetId,
            String graphFingerprint,
            int vertexCount,
            int edgeCount,
            long totalRequiredFlow,
            long totalReviewerCapacity,
            boolean invariantSatisfied,
            long invariantMaxFlow,
            List<AlgorithmMetricDto> algorithms,
            Map<String, List<String>> algorithmTraces
    ) {}

    public record ScalabilitySweepRequest(
            Integer startManuscripts,
            Integer endManuscripts,
            Integer stepSize,
            Double reviewerRatio,
            Integer warmupTrials,
            Integer measuredTrials,
            Long seed
    ) {}

    public record ScalabilityPointDto(
            int manuscriptCount,
            int reviewerCount,
            int totalVertices,
            int totalEdges,
            long maxFlow,
            double fordFulkersonMedianMs,
            double edmondsKarpMedianMs,
            double dinicMedianMs,
            int fordFulkersonAugmentations,
            int edmondsKarpAugmentations,
            int dinicAugmentations,
            boolean invariantVerified
    ) {}

    public record ScalabilitySweepResponse(
            long seed,
            int startManuscripts,
            int endManuscripts,
            int stepSize,
            List<ScalabilityPointDto> points,
            boolean allInvariantsVerified
    ) {}
}
