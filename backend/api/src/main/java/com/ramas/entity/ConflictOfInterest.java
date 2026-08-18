package com.ramas.entity;

import com.ramas.enums.ConflictType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "conflicts_of_interest", indexes = {
        @Index(name = "idx_conflicts_manuscript", columnList = "manuscript_id"),
        @Index(name = "idx_conflicts_reviewer", columnList = "reviewer_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_conflict_manuscript_reviewer", columnNames = {"manuscript_id", "reviewer_id"})
})
public class ConflictOfInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ConflictType conflictType;

    @Column(length = 255)
    private String reason;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public ConflictOfInterest() {
    }

    public ConflictOfInterest(Conference conference, Manuscript manuscript, Reviewer reviewer, ConflictType conflictType, String reason) {
        this.conference = conference;
        this.manuscript = manuscript;
        this.reviewer = reviewer;
        this.conflictType = conflictType;
        this.reason = reason;
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

    public ConflictType getConflictType() {
        return conflictType;
    }

    public void setConflictType(ConflictType conflictType) {
        this.conflictType = conflictType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConflictOfInterest that = (ConflictOfInterest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
