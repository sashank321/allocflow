package com.ramas.algorithm.allocation;

import com.ramas.algorithm.flow.FlowEdge;
import com.ramas.algorithm.flow.FlowNetwork;

import java.util.*;

/**
 * Constructs the canonical Bipartite Flow Network (S -> P -> R -> T).
 *
 * S -> P_i with capacity = requiredReviews
 * P_i -> R_j with capacity = 1 (only if eligible & conflict-free)
 * R_j -> T with capacity = reviewerCapacity
 */
public final class BipartiteGraphBuilder {

    public record BuildResult(
            FlowNetwork network,
            Map<String, Integer> manuscriptVertexMap,
            Map<String, Integer> reviewerVertexMap,
            Map<Integer, String> vertexToManuscriptMap,
            Map<Integer, String> vertexToReviewerMap,
            int eligibleEdgesCount,
            int conflictExclusionsCount,
            int totalRequiredReviews,
            int totalReviewerCapacity
    ) {}

    private BipartiteGraphBuilder() {
    }

    public static BuildResult buildNetwork(
            List<ManuscriptNode> manuscripts,
            List<ReviewerNode> reviewers,
            List<ConflictDeclaration> conflicts,
            int defaultRequiredReviews
    ) {
        Objects.requireNonNull(manuscripts, "manuscripts list cannot be null");
        Objects.requireNonNull(reviewers, "reviewers list cannot be null");

        int pCount = manuscripts.size();
        int rCount = reviewers.size();
        int totalVertices = pCount + rCount + 2;

        int source = 0;
        int sink = totalVertices - 1;

        FlowNetwork network = new FlowNetwork(totalVertices, source, sink);
        network.setVertexLabel(source, "SOURCE");
        network.setVertexLabel(sink, "SINK");

        Map<String, Integer> manuscriptVertexMap = new LinkedHashMap<>();
        Map<String, Integer> reviewerVertexMap = new LinkedHashMap<>();
        Map<Integer, String> vertexToManuscriptMap = new HashMap<>();
        Map<Integer, String> vertexToReviewerMap = new HashMap<>();

        // 1. Assign vertices to manuscripts (1 ... pCount)
        int currentV = 1;
        int totalRequiredReviews = 0;
        for (int i = 0; i < pCount; i++) {
            ManuscriptNode m = manuscripts.get(i);
            int v = currentV++;
            manuscriptVertexMap.put(m.id(), v);
            vertexToManuscriptMap.put(v, m.id());

            String cleanTitle = m.title().length() > 25 ? m.title().substring(0, 22) + "..." : m.title();
            network.setVertexLabel(v, String.format("P%d: %s", (i + 1), cleanTitle));

            int req = m.requiredReviews() > 0 ? m.requiredReviews() : defaultRequiredReviews;
            totalRequiredReviews += req;

            // Add S -> P_i edge
            network.addEdge(new FlowEdge(source, v, req, FlowEdge.EdgeType.SOURCE_TO_MANUSCRIPT, m.id(), null));
        }

        // 2. Assign vertices to reviewers (pCount + 1 ... pCount + rCount)
        int totalReviewerCapacity = 0;
        for (int j = 0; j < rCount; j++) {
            ReviewerNode r = reviewers.get(j);
            int v = currentV++;
            reviewerVertexMap.put(r.id(), v);
            vertexToReviewerMap.put(v, r.id());

            String cleanName = r.name().length() > 20 ? r.name().substring(0, 17) + "..." : r.name();
            network.setVertexLabel(v, String.format("R%d: %s", (j + 1), cleanName));

            int cap = Math.max(0, r.maxCapacity());
            totalReviewerCapacity += cap;

            // Add R_j -> T edge
            network.addEdge(new FlowEdge(v, sink, cap, FlowEdge.EdgeType.REVIEWER_TO_SINK, null, r.id()));
        }

        // Build quick conflict lookup table: "manuscriptId#reviewerId"
        Set<String> conflictSet = new HashSet<>();
        if (conflicts != null) {
            for (ConflictDeclaration cd : conflicts) {
                if (cd.manuscriptId() != null && cd.reviewerId() != null) {
                    conflictSet.add(cd.manuscriptId() + "#" + cd.reviewerId());
                }
            }
        }

        int eligibleEdges = 0;
        int conflictExclusions = 0;

        // 3. Connect eligible, conflict-free manuscripts to reviewers (P_i -> R_j)
        for (ManuscriptNode m : manuscripts) {
            int pVertex = manuscriptVertexMap.get(m.id());

            for (ReviewerNode r : reviewers) {
                int rVertex = reviewerVertexMap.get(r.id());

                // Check conflict: explicit COI declaration
                boolean hasConflict = conflictSet.contains(m.id() + "#" + r.id());

                // Institutional conflict or author conflict check
                if (!hasConflict && r.affiliation() != null && !r.affiliation().isBlank()) {
                    for (String aff : m.authorAffiliations()) {
                        if (r.affiliation().equalsIgnoreCase(aff)) {
                            hasConflict = true;
                            break;
                        }
                    }
                }
                if (!hasConflict && m.authorIds().contains(r.id())) {
                    hasConflict = true;
                }

                if (hasConflict) {
                    conflictExclusions++;
                    continue; // Strict exclusion: invalid edge never added
                }

                // Check topic/keyword compatibility
                CompatibilityCalculator.CompatibilityDetails compat = CompatibilityCalculator.evaluate(m, r);
                if (compat.eligible()) {
                    network.addEdge(new FlowEdge(
                            pVertex,
                            rVertex,
                            1L, // Capacity 1: reviewer reviews manuscript at most once
                            FlowEdge.EdgeType.MANUSCRIPT_TO_REVIEWER,
                            m.id(),
                            r.id()
                    ));
                    eligibleEdges++;
                }
            }
        }

        return new BuildResult(
                network,
                manuscriptVertexMap,
                reviewerVertexMap,
                vertexToManuscriptMap,
                vertexToReviewerMap,
                eligibleEdges,
                conflictExclusions,
                totalRequiredReviews,
                totalReviewerCapacity
        );
    }
}
