package com.ramas.dto;

import java.util.Set;
import java.util.UUID;

public class ExplanationDto {

    public record AssignmentExplanationDto(
            UUID manuscriptId,
            String manuscriptTitle,
            String manuscriptTrack,
            UUID reviewerId,
            String reviewerName,
            String reviewerAffiliation,
            int topicOverlapCount,
            Set<String> matchingTopics,
            int keywordOverlapCount,
            Set<String> matchingKeywords,
            double compatibilityScore,
            int reviewerWorkloadAssigned,
            int reviewerMaxCapacity,
            boolean conflictFree,
            String conflictVerificationDetails,
            String algorithmName,
            String algorithmRunId,
            long flow,
            String graphFingerprint,
            String explanationSummary
    ) {}
}
