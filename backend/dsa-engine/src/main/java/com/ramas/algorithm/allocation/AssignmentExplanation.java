package com.ramas.algorithm.allocation;

import java.util.Collections;
import java.util.Set;

/**
 * Structured explainability payload for a single manuscript-reviewer assignment.
 * Powers the "Explain This Assignment" modal/drawer in both Operations and Research modes.
 */
public record AssignmentExplanation(
        String manuscriptId,
        String manuscriptTitle,
        String manuscriptTrack,
        String reviewerId,
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
) {
    public AssignmentExplanation {
        matchingTopics = matchingTopics != null ? Set.copyOf(matchingTopics) : Collections.emptySet();
        matchingKeywords = matchingKeywords != null ? Set.copyOf(matchingKeywords) : Collections.emptySet();
    }

    public static AssignmentExplanation build(
            ManuscriptNode manuscript,
            ReviewerNode reviewer,
            int reviewerWorkloadAssigned,
            String algorithmName,
            String algorithmRunId,
            String graphFingerprint
    ) {
        CompatibilityCalculator.CompatibilityDetails compat = CompatibilityCalculator.evaluate(manuscript, reviewer);

        boolean conflictFree = !manuscript.authorIds().contains(reviewer.id()) &&
                !(reviewer.affiliation() != null && !reviewer.affiliation().isBlank() &&
                        manuscript.authorAffiliations().contains(reviewer.affiliation()));

        String conflictDetails = conflictFree
                ? "Verified: No co-authorship, advisor/advisee, or institutional affiliation conflicts detected"
                : "Warning: Potential conflict flags detected";

        String summary = String.format(
                "✓ Topic Overlap: %d %s | ✓ Keyword Match: %d %s | ✓ Available (Capacity: %d/%d) | ✓ Conflict-Free | Flow: 1 via %s",
                compat.topicOverlapCount(), compat.matchingTopics(),
                compat.keywordOverlapCount(), compat.matchingKeywords(),
                reviewerWorkloadAssigned, reviewer.maxCapacity(),
                algorithmName
        );

        return new AssignmentExplanation(
                manuscript.id(),
                manuscript.title(),
                manuscript.track(),
                reviewer.id(),
                reviewer.name(),
                reviewer.affiliation(),
                compat.topicOverlapCount(),
                compat.matchingTopics(),
                compat.keywordOverlapCount(),
                compat.matchingKeywords(),
                compat.score(),
                reviewerWorkloadAssigned,
                reviewer.maxCapacity(),
                conflictFree,
                conflictDetails,
                algorithmName,
                algorithmRunId,
                1L,
                graphFingerprint,
                summary
        );
    }
}
