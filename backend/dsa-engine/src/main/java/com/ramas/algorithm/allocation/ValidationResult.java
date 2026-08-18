package com.ramas.algorithm.allocation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates the results of an assignment validation check.
 */
public record ValidationResult(
        boolean valid,
        int totalAssignedPairs,
        int totalRequiredReviews,
        double coveragePercentage,
        int fullySatisfiedManuscripts,
        int partiallySatisfiedManuscripts,
        int zeroReviewManuscripts,
        Map<String, Integer> reviewerWorkloadMap,
        List<String> validationErrors,
        List<String> warnings
) {
    public ValidationResult {
        reviewerWorkloadMap = reviewerWorkloadMap != null ? Map.copyOf(reviewerWorkloadMap) : Collections.emptyMap();
        validationErrors = validationErrors != null ? List.copyOf(validationErrors) : Collections.emptyList();
        warnings = warnings != null ? List.copyOf(warnings) : Collections.emptyList();
    }
}
