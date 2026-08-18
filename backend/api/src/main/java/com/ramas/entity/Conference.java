package com.ramas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "conferences", indexes = {
        @Index(name = "idx_conferences_code", columnList = "code", unique = true)
})
public class Conference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 30)
    private String acronym;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private Instant submissionDeadline;

    @Column
    private Instant reviewDeadline;

    @Column(nullable = false)
    private int requiredReviewsPerPaper = 2;

    @Column(nullable = false)
    private int defaultReviewerCapacity = 4;

    @Column(nullable = false, length = 30)
    private String status = "ACTIVE";

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Conference() {
    }

    public Conference(String code, String name, String acronym, String description, int requiredReviewsPerPaper, int defaultReviewerCapacity) {
        this.code = code;
        this.name = name;
        this.acronym = acronym;
        this.description = description;
        this.requiredReviewsPerPaper = requiredReviewsPerPaper;
        this.defaultReviewerCapacity = defaultReviewerCapacity;
        this.status = "ACTIVE";
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getSubmissionDeadline() {
        return submissionDeadline;
    }

    public void setSubmissionDeadline(Instant submissionDeadline) {
        this.submissionDeadline = submissionDeadline;
    }

    public Instant getReviewDeadline() {
        return reviewDeadline;
    }

    public void setReviewDeadline(Instant reviewDeadline) {
        this.reviewDeadline = reviewDeadline;
    }

    public int getRequiredReviewsPerPaper() {
        return requiredReviewsPerPaper;
    }

    public void setRequiredReviewsPerPaper(int requiredReviewsPerPaper) {
        this.requiredReviewsPerPaper = requiredReviewsPerPaper;
    }

    public int getDefaultReviewerCapacity() {
        return defaultReviewerCapacity;
    }

    public void setDefaultReviewerCapacity(int defaultReviewerCapacity) {
        this.defaultReviewerCapacity = defaultReviewerCapacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conference that = (Conference) o;
        return Objects.equals(id, that.id) || Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
