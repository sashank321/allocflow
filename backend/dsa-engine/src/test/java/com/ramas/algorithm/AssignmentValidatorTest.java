package com.ramas.algorithm;

import com.ramas.algorithm.allocation.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AssignmentValidatorTest {

    @Test
    @DisplayName("Validator flags reviewer capacity violation when assignments exceed max capacity")
    void testCapacityViolation() {
        ManuscriptNode p1 = new ManuscriptNode("P1", "Paper 1", "AI", 1, Set.of("AI"), Set.of(), Set.of("A1"), Set.of());
        ManuscriptNode p2 = new ManuscriptNode("P2", "Paper 2", "AI", 1, Set.of("AI"), Set.of(), Set.of("A2"), Set.of());
        ReviewerNode r1 = new ReviewerNode("R1", "Dr. Bob", "bob@test.org", "Univ", 1, true, true, Set.of("AI"), Set.of()); // max capacity 1

        List<AssignmentExtractor.AssignedPair> assignments = List.of(
                new AssignmentExtractor.AssignedPair("P1", "R1", 1L, 1, 3),
                new AssignmentExtractor.AssignedPair("P2", "R1", 1L, 2, 3)
        );

        ValidationResult res = AssignmentValidator.validate(
                assignments,
                List.of(p1, p2),
                List.of(r1),
                Collections.emptyList(),
                1
        );

        assertThat(res.valid()).isFalse();
        assertThat(res.validationErrors()).anyMatch(e -> e.contains("Capacity exceeded"));
    }

    @Test
    @DisplayName("Validator flags conflict violation when author reviews own paper")
    void testAuthorConflictViolation() {
        ManuscriptNode p1 = new ManuscriptNode("P1", "Paper 1", "AI", 1, Set.of("AI"), Set.of(), Set.of("R1"), Set.of());
        ReviewerNode r1 = new ReviewerNode("R1", "Dr. Author-Reviewer", "author@test.org", "Univ", 3, true, true, Set.of("AI"), Set.of());

        List<AssignmentExtractor.AssignedPair> assignments = List.of(
                new AssignmentExtractor.AssignedPair("P1", "R1", 1L, 1, 2)
        );

        ValidationResult res = AssignmentValidator.validate(
                assignments,
                List.of(p1),
                List.of(r1),
                Collections.emptyList(),
                1
        );

        assertThat(res.valid()).isFalse();
        assertThat(res.validationErrors()).anyMatch(e -> e.contains("Conflict violation"));
    }

    @Test
    @DisplayName("Validator flags duplicate assignment when same reviewer assigned twice to same paper")
    void testDuplicateAssignmentViolation() {
        ManuscriptNode p1 = new ManuscriptNode("P1", "Paper 1", "AI", 2, Set.of("AI"), Set.of(), Set.of("A1"), Set.of());
        ReviewerNode r1 = new ReviewerNode("R1", "Dr. Alice", "alice@test.org", "Univ", 4, true, true, Set.of("AI"), Set.of());

        List<AssignmentExtractor.AssignedPair> assignments = List.of(
                new AssignmentExtractor.AssignedPair("P1", "R1", 1L, 1, 2),
                new AssignmentExtractor.AssignedPair("P1", "R1", 1L, 1, 2)
        );

        ValidationResult res = AssignmentValidator.validate(
                assignments,
                List.of(p1),
                List.of(r1),
                Collections.emptyList(),
                2
        );

        assertThat(res.valid()).isFalse();
        assertThat(res.validationErrors()).anyMatch(e -> e.contains("Duplicate assignment"));
    }
}
