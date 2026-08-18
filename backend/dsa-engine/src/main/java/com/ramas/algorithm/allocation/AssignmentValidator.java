package com.ramas.algorithm.allocation;

import java.util.*;

/**
 * Validates assignment results against formal mathematical and business rules:
 * 1. Zero conflict-of-interest violations
 * 2. Reviewer capacity limits strictly observed (workload <= capacity)
 * 3. No duplicate reviewer assignments to the same manuscript
 * 4. All assignments correspond to eligible, active reviewers
 * 5. Accurate calculation of coverage and fulfillment
 */
public final class AssignmentValidator {

    private AssignmentValidator() {
    }

    public static ValidationResult validate(
            List<AssignmentExtractor.AssignedPair> assignments,
            List<ManuscriptNode> manuscripts,
            List<ReviewerNode> reviewers,
            List<ConflictDeclaration> conflicts,
            int defaultRequiredReviews
    ) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        Map<String, ManuscriptNode> manuscriptMap = new HashMap<>();
        for (ManuscriptNode m : manuscripts) {
            manuscriptMap.put(m.id(), m);
        }

        Map<String, ReviewerNode> reviewerMap = new HashMap<>();
        for (ReviewerNode r : reviewers) {
            reviewerMap.put(r.id(), r);
        }

        // Build conflict lookup table
        Set<String> conflictSet = new HashSet<>();
        if (conflicts != null) {
            for (ConflictDeclaration cd : conflicts) {
                if (cd.manuscriptId() != null && cd.reviewerId() != null) {
                    conflictSet.add(cd.manuscriptId() + "#" + cd.reviewerId());
                }
            }
        }

        // Track per-reviewer workload and per-manuscript reviewers
        Map<String, Integer> reviewerWorkload = new LinkedHashMap<>();
        for (ReviewerNode r : reviewers) {
            reviewerWorkload.put(r.id(), 0);
        }

        Map<String, Set<String>> manuscriptAssignedReviewers = new LinkedHashMap<>();
        for (ManuscriptNode m : manuscripts) {
            manuscriptAssignedReviewers.put(m.id(), new HashSet<>());
        }

        // 1. Verify every assignment pair
        for (AssignmentExtractor.AssignedPair pair : assignments) {
            String mId = pair.manuscriptId();
            String rId = pair.reviewerId();

            ManuscriptNode m = manuscriptMap.get(mId);
            ReviewerNode r = reviewerMap.get(rId);

            if (m == null) {
                errors.add(String.format("Invalid assignment: Manuscript '%s' does not exist in dataset", mId));
                continue;
            }
            if (r == null) {
                errors.add(String.format("Invalid assignment: Reviewer '%s' does not exist in dataset", rId));
                continue;
            }

            // Check duplicate
            Set<String> assignedToM = manuscriptAssignedReviewers.get(mId);
            if (assignedToM.contains(rId)) {
                errors.add(String.format("Duplicate assignment: Reviewer '%s' assigned multiple times to Manuscript '%s'", r.name(), m.title()));
            } else {
                assignedToM.add(rId);
            }

            // Check conflict
            if (conflictSet.contains(mId + "#" + rId)) {
                errors.add(String.format("Conflict violation: Explicit COI declared between Manuscript '%s' and Reviewer '%s'", m.title(), r.name()));
            }
            if (m.authorIds().contains(rId)) {
                errors.add(String.format("Conflict violation: Reviewer '%s' is an author of Manuscript '%s'", r.name(), m.title()));
            }
            if (r.affiliation() != null && !r.affiliation().isBlank() && m.authorAffiliations().contains(r.affiliation())) {
                errors.add(String.format("Institutional conflict: Reviewer '%s' shares affiliation '%s' with Manuscript '%s'", r.name(), r.affiliation(), m.title()));
            }

            // Check reviewer status
            if (!r.active()) {
                errors.add(String.format("Ineligibility violation: Reviewer '%s' is inactive", r.name()));
            }
            if (!r.available()) {
                errors.add(String.format("Ineligibility violation: Reviewer '%s' is unavailable", r.name()));
            }

            // Increment workload
            reviewerWorkload.put(rId, reviewerWorkload.getOrDefault(rId, 0) + 1);
        }

        // 2. Check reviewer capacities
        for (ReviewerNode r : reviewers) {
            int currentWorkload = reviewerWorkload.getOrDefault(r.id(), 0);
            if (currentWorkload > r.maxCapacity()) {
                errors.add(String.format("Capacity exceeded: Reviewer '%s' assigned %d reviews (Max capacity is %d)",
                        r.name(), currentWorkload, r.maxCapacity()));
            }
        }

        // 3. Check manuscript fulfillment
        int totalRequired = 0;
        int fullySatisfied = 0;
        int partiallySatisfied = 0;
        int zeroReview = 0;

        for (ManuscriptNode m : manuscripts) {
            int req = m.requiredReviews() > 0 ? m.requiredReviews() : defaultRequiredReviews;
            totalRequired += req;
            int actualAssigned = manuscriptAssignedReviewers.get(m.id()).size();

            if (actualAssigned >= req) {
                fullySatisfied++;
            } else if (actualAssigned > 0) {
                partiallySatisfied++;
                warnings.add(String.format("Partial allocation: Manuscript '%s' received %d/%d required reviews",
                        m.title(), actualAssigned, req));
            } else {
                zeroReview++;
                warnings.add(String.format("Unassigned manuscript: Manuscript '%s' received 0/%d required reviews",
                        m.title(), req));
            }
        }

        double coveragePercentage = totalRequired > 0
                ? ((double) assignments.size() / totalRequired) * 100.0
                : 100.0;

        boolean isValid = errors.isEmpty();

        return new ValidationResult(
                isValid,
                assignments.size(),
                totalRequired,
                coveragePercentage,
                fullySatisfied,
                partiallySatisfied,
                zeroReview,
                reviewerWorkload,
                errors,
                warnings
        );
    }
}
