package com.ramas.algorithm.flow;

import java.util.*;

/**
 * Representation of a flow network using adjacency lists.
 * Supports deep cloning to ensure that algorithms (Ford-Fulkerson, Edmonds-Karp, Dinic)
 * execute on identical, isolated graphs without mutating the canonical template.
 */
public class FlowNetwork {
    private final int vertexCount;
    private final int source;
    private final int sink;
    private final List<List<FlowEdge>> adj;
    private final List<String> vertexLabels;
    private final List<FlowEdge> originalEdges;
    private String cachedFingerprint;

    public FlowNetwork(int vertexCount, int source, int sink) {
        if (vertexCount < 2) {
            throw new IllegalArgumentException("Network must have at least 2 vertices (source and sink)");
        }
        if (source < 0 || source >= vertexCount || sink < 0 || sink >= vertexCount || source == sink) {
            throw new IllegalArgumentException("Invalid source/sink vertex indices");
        }
        this.vertexCount = vertexCount;
        this.source = source;
        this.sink = sink;
        this.adj = new ArrayList<>(vertexCount);
        this.vertexLabels = new ArrayList<>(vertexCount);
        this.originalEdges = new ArrayList<>();

        for (int v = 0; v < vertexCount; v++) {
            this.adj.add(new ArrayList<>());
            this.vertexLabels.add("V" + v);
        }
        this.vertexLabels.set(source, "SOURCE");
        this.vertexLabels.set(sink, "SINK");
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getSource() {
        return source;
    }

    public int getSink() {
        return sink;
    }

    public void setVertexLabel(int vertex, String label) {
        validateVertex(vertex);
        this.vertexLabels.set(vertex, label != null ? label : "V" + vertex);
        this.cachedFingerprint = null;
    }

    public String getVertexLabel(int vertex) {
        validateVertex(vertex);
        return this.vertexLabels.get(vertex);
    }

    public List<String> getAllVertexLabels() {
        return Collections.unmodifiableList(vertexLabels);
    }

    public void addEdge(FlowEdge edge) {
        validateVertex(edge.getFrom());
        validateVertex(edge.getTo());

        // Canonical representation: single edge object shared in both endpoints' adjacency lists
        adj.get(edge.getFrom()).add(edge);
        adj.get(edge.getTo()).add(edge);
        originalEdges.add(edge);

        this.cachedFingerprint = null;
    }

    public List<FlowEdge> adj(int vertex) {
        validateVertex(vertex);
        return Collections.unmodifiableList(adj.get(vertex));
    }

    public List<FlowEdge> getOriginalEdges() {
        return Collections.unmodifiableList(originalEdges);
    }

    public int getEdgeCount() {
        return originalEdges.size();
    }

    public long getTotalSourceCapacity() {
        long sum = 0;
        for (FlowEdge edge : originalEdges) {
            if (edge.getFrom() == source) {
                sum += edge.getCapacity();
            }
        }
        return sum;
    }

    public long getTotalSinkCapacity() {
        long sum = 0;
        for (FlowEdge edge : originalEdges) {
            if (edge.getTo() == sink) {
                sum += edge.getCapacity();
            }
        }
        return sum;
    }

    public String getFingerprint() {
        if (cachedFingerprint == null) {
            cachedFingerprint = GraphFingerprint.compute(this);
        }
        return cachedFingerprint;
    }

    /**
     * Creates an exact deep clone of this network with all flows reset to 0.
     * Guarantees complete algorithm isolation.
     */
    public FlowNetwork deepClone() {
        FlowNetwork copy = new FlowNetwork(this.vertexCount, this.source, this.sink);
        for (int v = 0; v < this.vertexCount; v++) {
            copy.setVertexLabel(v, this.vertexLabels.get(v));
        }
        for (FlowEdge edge : this.originalEdges) {
            FlowEdge edgeCopy = new FlowEdge(
                    edge.getFrom(),
                    edge.getTo(),
                    edge.getCapacity(),
                    edge.getEdgeType(),
                    edge.getManuscriptId(),
                    edge.getReviewerId()
            );
            copy.addEdge(edgeCopy);
        }
        copy.cachedFingerprint = this.cachedFingerprint;
        return copy;
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= vertexCount) {
            throw new IndexOutOfBoundsException("Vertex index out of bounds: " + v + " (V=" + vertexCount + ")");
        }
    }
}
