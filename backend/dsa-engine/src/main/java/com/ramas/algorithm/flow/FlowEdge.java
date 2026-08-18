package com.ramas.algorithm.flow;

import java.util.Objects;

/**
 * Directed edge in a flow network with capacity, current flow,
 * and residual flow calculations.
 *
 * Implements the standard canonical network flow edge representation:
 * The single edge object is referenced in both `from` and `to` adjacency lists.
 */
public class FlowEdge {
    public enum EdgeType {
        SOURCE_TO_MANUSCRIPT,
        MANUSCRIPT_TO_REVIEWER,
        REVIEWER_TO_SINK,
        AUXILIARY
    }

    private final int from;
    private final int to;
    private final long capacity;
    private long flow;

    // Domain metadata for explainability & traceability
    private final EdgeType edgeType;
    private final String manuscriptId;
    private final String reviewerId;

    public FlowEdge(int from, int to, long capacity) {
        this(from, to, capacity, EdgeType.AUXILIARY, null, null);
    }

    public FlowEdge(int from, int to, long capacity, EdgeType edgeType, String manuscriptId, String reviewerId) {
        if (from < 0 || to < 0) {
            throw new IllegalArgumentException("Vertex indices must be non-negative");
        }
        if (capacity < 0) {
            throw new IllegalArgumentException("Edge capacity must be non-negative");
        }
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = 0L;
        this.edgeType = edgeType != null ? edgeType : EdgeType.AUXILIARY;
        this.manuscriptId = manuscriptId;
        this.reviewerId = reviewerId;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public long getCapacity() {
        return capacity;
    }

    public long getFlow() {
        return flow;
    }

    public EdgeType getEdgeType() {
        return edgeType;
    }

    public String getManuscriptId() {
        return manuscriptId;
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public int other(int vertex) {
        if (vertex == from) {
            return to;
        } else if (vertex == to) {
            return from;
        } else {
            throw new IllegalArgumentException("Illegal endpoint: " + vertex);
        }
    }

    public long residualCapacityTo(int vertex) {
        if (vertex == from) {
            return flow; // backward edge residual capacity
        } else if (vertex == to) {
            return capacity - flow; // forward edge residual capacity
        } else {
            throw new IllegalArgumentException("Illegal endpoint: " + vertex);
        }
    }

    public void addResidualFlowTo(int vertex, long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("Delta must be non-negative");
        }
        if (vertex == from) {
            flow -= delta; // backward edge pushes negative forward flow
        } else if (vertex == to) {
            flow += delta; // forward edge pushes positive forward flow
        } else {
            throw new IllegalArgumentException("Illegal endpoint: " + vertex);
        }
    }

    public void resetFlow() {
        this.flow = 0L;
    }

    /**
     * Creates an exact detached copy of this edge (flow reset to 0).
     */
    public FlowEdge cloneDetached() {
        FlowEdge copy = new FlowEdge(from, to, capacity, edgeType, manuscriptId, reviewerId);
        copy.flow = this.flow;
        return copy;
    }

    @Override
    public String toString() {
        return String.format("%d->%d (%d/%d) [%s]", from, to, flow, capacity, edgeType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowEdge flowEdge = (FlowEdge) o;
        return from == flowEdge.from && to == flowEdge.to && capacity == flowEdge.capacity &&
                Objects.equals(manuscriptId, flowEdge.manuscriptId) &&
                Objects.equals(reviewerId, flowEdge.reviewerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, capacity, manuscriptId, reviewerId);
    }
}
