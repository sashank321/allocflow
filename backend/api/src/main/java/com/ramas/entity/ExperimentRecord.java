package com.ramas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "experiment_records", indexes = {
        @Index(name = "idx_experiments_dataset", columnList = "dataset_id"),
        @Index(name = "idx_experiments_created", columnList = "created_at")
})
public class ExperimentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(name = "dataset_id", nullable = false, length = 100)
    private String datasetId;

    @Column(nullable = false)
    private long seed;

    @Column(nullable = false)
    private int manuscriptCount;

    @Column(nullable = false)
    private int reviewerCount;

    @Column(nullable = false)
    private int totalVertices;

    @Column(nullable = false)
    private int totalEdges;

    @NotBlank
    @Column(name = "graph_fingerprint", nullable = false, length = 64)
    private String graphFingerprint;

    @Column(nullable = false)
    private long maxFlow;

    @Column(nullable = false)
    private double fordFulkersonMedianMs;

    @Column(nullable = false)
    private double edmondsKarpMedianMs;

    @Column(nullable = false)
    private double dinicMedianMs;

    @Column(nullable = false)
    private int fordFulkersonAugmentations;

    @Column(nullable = false)
    private int edmondsKarpAugmentations;

    @Column(nullable = false)
    private int dinicAugmentations;

    @Column(nullable = false)
    private boolean invariantVerified = true;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public ExperimentRecord() {
    }

    public ExperimentRecord(String datasetId, long seed, int manuscriptCount, int reviewerCount,
                            int totalVertices, int totalEdges, String graphFingerprint, long maxFlow,
                            double fordFulkersonMedianMs, double edmondsKarpMedianMs, double dinicMedianMs,
                            int fordFulkersonAugmentations, int edmondsKarpAugmentations, int dinicAugmentations,
                            boolean invariantVerified) {
        this.datasetId = datasetId;
        this.seed = seed;
        this.manuscriptCount = manuscriptCount;
        this.reviewerCount = reviewerCount;
        this.totalVertices = totalVertices;
        this.totalEdges = totalEdges;
        this.graphFingerprint = graphFingerprint;
        this.maxFlow = maxFlow;
        this.fordFulkersonMedianMs = fordFulkersonMedianMs;
        this.edmondsKarpMedianMs = edmondsKarpMedianMs;
        this.dinicMedianMs = dinicMedianMs;
        this.fordFulkersonAugmentations = fordFulkersonAugmentations;
        this.edmondsKarpAugmentations = edmondsKarpAugmentations;
        this.dinicAugmentations = dinicAugmentations;
        this.invariantVerified = invariantVerified;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public int getManuscriptCount() {
        return manuscriptCount;
    }

    public void setManuscriptCount(int manuscriptCount) {
        this.manuscriptCount = manuscriptCount;
    }

    public int getReviewerCount() {
        return reviewerCount;
    }

    public void setReviewerCount(int reviewerCount) {
        this.reviewerCount = reviewerCount;
    }

    public int getTotalVertices() {
        return totalVertices;
    }

    public void setTotalVertices(int totalVertices) {
        this.totalVertices = totalVertices;
    }

    public int getTotalEdges() {
        return totalEdges;
    }

    public void setTotalEdges(int totalEdges) {
        this.totalEdges = totalEdges;
    }

    public String getGraphFingerprint() {
        return graphFingerprint;
    }

    public void setGraphFingerprint(String graphFingerprint) {
        this.graphFingerprint = graphFingerprint;
    }

    public long getMaxFlow() {
        return maxFlow;
    }

    public void setMaxFlow(long maxFlow) {
        this.maxFlow = maxFlow;
    }

    public double getFordFulkersonMedianMs() {
        return fordFulkersonMedianMs;
    }

    public void setFordFulkersonMedianMs(double fordFulkersonMedianMs) {
        this.fordFulkersonMedianMs = fordFulkersonMedianMs;
    }

    public double getEdmondsKarpMedianMs() {
        return edmondsKarpMedianMs;
    }

    public void setEdmondsKarpMedianMs(double edmondsKarpMedianMs) {
        this.edmondsKarpMedianMs = edmondsKarpMedianMs;
    }

    public double getDinicMedianMs() {
        return dinicMedianMs;
    }

    public void setDinicMedianMs(double dinicMedianMs) {
        this.dinicMedianMs = dinicMedianMs;
    }

    public int getFordFulkersonAugmentations() {
        return fordFulkersonAugmentations;
    }

    public void setFordFulkersonAugmentations(int fordFulkersonAugmentations) {
        this.fordFulkersonAugmentations = fordFulkersonAugmentations;
    }

    public int getEdmondsKarpAugmentations() {
        return edmondsKarpAugmentations;
    }

    public void setEdmondsKarpAugmentations(int edmondsKarpAugmentations) {
        this.edmondsKarpAugmentations = edmondsKarpAugmentations;
    }

    public int getDinicAugmentations() {
        return dinicAugmentations;
    }

    public void setDinicAugmentations(int dinicAugmentations) {
        this.dinicAugmentations = dinicAugmentations;
    }

    public boolean isInvariantVerified() {
        return invariantVerified;
    }

    public void setInvariantVerified(boolean invariantVerified) {
        this.invariantVerified = invariantVerified;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
