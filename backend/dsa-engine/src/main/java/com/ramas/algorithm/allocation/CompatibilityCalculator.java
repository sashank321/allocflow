package com.ramas.algorithm.allocation;

import java.util.*;

/**
 * Deterministic and explainable compatibility calculator.
 * Evaluates topic overlap, keyword matching, and reviewer availability.
 *
 * NOTE: Compatibility determines EDGE ELIGIBILITY (capacity = 1).
 * The allocation itself is computed strictly by the bipartite Max-Flow algorithm.
 */
public final class CompatibilityCalculator {

    private CompatibilityCalculator() {
    }

    public record CompatibilityDetails(
            boolean eligible,
            int topicOverlapCount,
            int keywordOverlapCount,
            double score,
            Set<String> matchingTopics,
            Set<String> matchingKeywords,
            String explanation
    ) {}

    public static CompatibilityDetails evaluate(ManuscriptNode manuscript, ReviewerNode reviewer) {
        if (!reviewer.active() || !reviewer.available()) {
            return new CompatibilityDetails(
                    false,
                    0,
                    0,
                    0.0,
                    Collections.emptySet(),
                    Collections.emptySet(),
                    "Reviewer is inactive or marked unavailable"
            );
        }

        // Calculate topic overlap (case-insensitive)
        Set<String> matchingTopics = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (String rTopic : reviewer.topics()) {
            for (String mTopic : manuscript.topics()) {
                if (rTopic.equalsIgnoreCase(mTopic)) {
                    matchingTopics.add(rTopic);
                }
            }
        }

        // Calculate keyword overlap (case-insensitive)
        Set<String> matchingKeywords = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (String rKey : reviewer.keywords()) {
            for (String mKey : manuscript.keywords()) {
                if (rKey.equalsIgnoreCase(mKey)) {
                    matchingKeywords.add(rKey);
                }
            }
        }

        int topicCount = matchingTopics.size();
        int keyCount = matchingKeywords.size();

        // Eligibility rule:
        // If manuscript specifies topics/keywords, reviewer must match at least one topic or keyword.
        // If manuscript has no topics/keywords, all active available reviewers are eligible.
        boolean hasConstraints = !manuscript.topics().isEmpty() || !manuscript.keywords().isEmpty();
        boolean eligible = !hasConstraints || (topicCount > 0 || keyCount > 0);

        // Deterministic explainable score:
        // 60% topic overlap ratio + 40% keyword overlap ratio (or 1.0 if no constraints)
        double score;
        if (!eligible) {
            score = 0.0;
        } else if (!hasConstraints) {
            score = 1.0;
        } else {
            double topicRatio = manuscript.topics().isEmpty() ? 0.5 : (double) topicCount / manuscript.topics().size();
            double keyRatio = manuscript.keywords().isEmpty() ? 0.5 : (double) keyCount / manuscript.keywords().size();
            score = Math.min(1.0, (0.6 * topicRatio) + (0.4 * keyRatio));
        }

        String explanation = eligible
                ? String.format("Eligible: %d topic matches %s, %d keyword matches %s (Score: %.2f)",
                topicCount, matchingTopics, keyCount, matchingKeywords, score)
                : "Ineligible: No matching topics or keywords found";

        return new CompatibilityDetails(
                eligible,
                topicCount,
                keyCount,
                score,
                matchingTopics,
                matchingKeywords,
                explanation
        );
    }
}
