package com.ramas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "assignments", indexes = {
        @Index(name = "idx_assignments_run", columnList = "assignment_run_id"),
        @Index(name = "idx_assignments_conference", columnList = "conference_id"),
        @Index(name = "idx_assignments_manuscript", columnList = "manuscript_id"),
        @Index(name = "idx_assignments_reviewer", columnList = "reviewer_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_assignment_run_paper_reviewer", columnNames = {"assignment_run_id", "manuscript_id", "reviewer_id"})
})
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_run_id", nullable = false)
    private AssignmentRun assignmentRun;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conference_id", nullable = false)
    private Conference conference;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manuscript_id", nullable = false)
    private Manuscript manuscript;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Reviewer reviewer;

    @Column(nullable = false)
    private long flow = 1L;

    @Column(nullable = false)
    private boolean isManualOverride = false;

    @Column(length = 255)
    private String overrideReason;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Assignment() {
    }

    public Assignment(AssignmentRun assignmentRun, Conference conference, Manuscript manuscript, Reviewer reviewer, long flow) {
        this.assignmentRun = assignmentRun;
        this.conference = conference;
        this.manuscript = manuscript;
        this.reviewer = reviewer;
        this.flow = flow;
        this.isManualOverride = false;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AssignmentRun getAssignmentRun() {
        return assignmentRun;
    }

    public void setAssignmentRun(AssignmentRun assignmentRun) {
        this.assignmentRun = assignmentRun;
    }

    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }

    public Manuscript getManuscript() {
        return manuscript;
    }

    public void setManuscript(Manuscript manuscript) {
        this.manuscript = manuscript;
    }

    public Reviewer getReviewer() {
        return reviewer;
    }

    public void setReviewer(Reviewer reviewer) {
        this.reviewer = reviewer;
    }

    public long getFlow() {
        return flow;
    }

    public void setFlow(long flow) {
        this.flow = flow;
    }

    public boolean isManualOverride() {
        return isManualOverride;
    }

    public void setManualOverride(boolean manualOverride) {
        isManualOverride = manualOverride;
    }

    public String getOverrideReason() {
        return overrideReason;
    }

    public void setOverrideReason(String overrideReason) {
        this.overrideReason = overrideReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignment that = (Assignment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
