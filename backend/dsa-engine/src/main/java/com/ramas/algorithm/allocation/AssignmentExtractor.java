package com.ramas.algorithm.allocation;

import com.ramas.algorithm.flow.FlowEdge;
import com.ramas.algorithm.flow.FlowNetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Extracts assigned manuscript-reviewer pairs from a solved FlowNetwork.
 */
public final class AssignmentExtractor {

    public record AssignedPair(
            String manuscriptId,
            String reviewerId,
            long flow,
            int manuscriptVertex,
            int reviewerVertex
    ) {}

    private AssignmentExtractor() {
    }

    public static List<AssignedPair> extractAssignments(FlowNetwork solvedNetwork) {
        List<AssignedPair> assignments = new ArrayList<>();

        for (FlowEdge edge : solvedNetwork.getOriginalEdges()) {
            if (edge.getEdgeType() == FlowEdge.EdgeType.MANUSCRIPT_TO_REVIEWER && edge.getFlow() > 0) {
                assignments.add(new AssignedPair(
                        edge.getManuscriptId(),
                        edge.getReviewerId(),
                        edge.getFlow(),
                        edge.getFrom(),
                        edge.getTo()
                ));
            }
        }

        return Collections.unmodifiableList(assignments);
    }
}
