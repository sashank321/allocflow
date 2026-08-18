package com.ramas.algorithm.evaluation;

/**
 * Configuration parameters for generating deterministic, reproducible synthetic datasets.
 */
public record SyntheticDatasetConfig(
        int manuscriptCount,
        int reviewerCount,
        int requiredReviewsPerPaper,
        int reviewerCapacity,
        double eligibilityProbability,
        double conflictProbability,
        int topicCount,
        long randomSeed
) {
    public static SyntheticDatasetConfig defaultConfig(int manuscripts, int reviewers, long seed) {
        return new SyntheticDatasetConfig(
                manuscripts,
                reviewers,
                2,     // 2 reviews per manuscript
                4,     // max 4 reviews per reviewer
                0.35,  // 35% topic/keyword overlap probability
                0.05,  // 5% conflict probability
                8,     // 8 topic categories
                seed
        );
    }
}
