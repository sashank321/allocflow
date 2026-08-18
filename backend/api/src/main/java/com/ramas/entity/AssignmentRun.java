package com.ramas.entity;

import com.ramas.enums.AlgorithmType;
import com.ramas.enums.AssignmentRunStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "assignment_runs", indexes = {
        @Index(name = "idx_assignment_runs_conference", columnList = "conference_id"),
        @Index(name = "idx_assignment_runs_fingerprint", columnList = "graph_fingerprint"),
        @Index(name = "idx_assignment_runs_status", columnList = "status")
})
public class AssignmentRun {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conference_id", nullable = false)
    private Conference conference;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AlgorithmType algorithm;

    @NotBlank
    @Column(name = "graph_fingerprint", nullable = false, length = 64)
    private String graphFingerprint;

    @Column(nullable = false)
    private int totalManuscripts;

    @Column(nullable = false)
    private int totalReviewers;

    @Column(nullable = false)
    private int totalVertices;

    @Column(nullable = false)
    private int totalEdges;

    @Column(nullable = false)
    private long totalRequiredFlow;

    @Column(nullable = false)
    private long achievedFlow;

    @Column(nullable = false)
    private double coveragePercentage;

    @Column(nullable = false)
    private double durationMs;

    @Column(nullable = false)
    private int augmentationsCount;

    @Column(nullable = false)
    private int phasesCount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AssignmentRunStatus status = AssignmentRunStatus.SIMULATED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "committed_by_user_id")
    private User committedBy;

    @Column
    private Instant committedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public AssignmentRun() {
    }

    public AssignmentRun(Conference conference, AlgorithmType algorithm, String graphFingerprint,
                         int totalManuscripts, int totalReviewers, int totalVertices, int totalEdges,
                         long totalRequiredFlow, long achievedFlow, double coveragePercentage,
                         double durationMs, int augmentationsCount, int phasesCount) {
        this.conference = conference;
        this.algorithm = algorithm;
        this.graphFingerprint = graphFingerprint;
        this.totalManuscripts = totalManuscripts;
        this.totalReviewers = totalReviewers;
        this.totalVertices = totalVertices;
        this.totalEdges = totalEdges;
        this.totalRequiredFlow = totalRequiredFlow;
        this.achievedFlow = achievedFlow;
        this.coveragePercentage = coveragePercentage;
        this.durationMs = durationMs;
        this.augmentationsCount = augmentationsCount;
        this.phasesCount = phasesCount;
        this.status = AssignmentRunStatus.SIMULATED;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }

    public AlgorithmType getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(AlgorithmType algorithm) {
        this.algorithm = algorithm;
    }

    public String getGraphFingerprint() {
        return graphFingerprint;
    }

    public void setGraphFingerprint(String graphFingerprint) {
        this.graphFingerprint = graphFingerprint;
    }

    public int getTotalManuscripts() {
        return totalManuscripts;
    }

    public void setTotalManuscripts(int totalManuscripts) {
        this.totalManuscripts = totalManuscripts;
    }

    public int getTotalReviewers() {
        return totalReviewers;
    }

    public void setTotalReviewers(int totalReviewers) {
        this.totalReviewers = totalReviewers;
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

    public long getTotalRequiredFlow() {
        return totalRequiredFlow;
    }

    public void setTotalRequiredFlow(long totalRequiredFlow) {
        this.totalRequiredFlow = totalRequiredFlow;
    }

    public long getAchievedFlow() {
        return achievedFlow;
    }

    public void setAchievedFlow(long achievedFlow) {
        this.achievedFlow = achievedFlow;
    }

    public double getCoveragePercentage() {
        return coveragePercentage;
    }

    public void setCoveragePercentage(double coveragePercentage) {
        this.coveragePercentage = coveragePercentage;
    }

    public double getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(double durationMs) {
        this.durationMs = durationMs;
    }

    public int getAugmentationsCount() {
        return augmentationsCount;
    }

    public void setAugmentationsCount(int augmentationsCount) {
        this.augmentationsCount = augmentationsCount;
    }

    public int getPhasesCount() {
        return phasesCount;
    }

    public void setPhasesCount(int phasesCount) {
        this.phasesCount = phasesCount;
    }

    public AssignmentRunStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentRunStatus status) {
        this.status = status;
    }

    public User getCommittedBy() {
        return committedBy;
    }

    public void setCommittedBy(User committedBy) {
        this.committedBy = committedBy;
    }

    public Instant getCommittedAt() {
        return committedAt;
    }

    public void setCommittedAt(Instant committedAt) {
        this.committedAt = committedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentRun that = (AssignmentRun) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
