package com.ramas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ConferenceDtos {

    public record ConferenceRequest(
            @NotBlank String code,
            @NotBlank String name,
            String acronym,
            String description,
            Instant submissionDeadline,
            Instant reviewDeadline,
            int requiredReviewsPerPaper,
            int defaultReviewerCapacity
    ) {}

    public record ConferenceDto(
            UUID id,
            String code,
            String name,
            String acronym,
            String description,
            Instant submissionDeadline,
            Instant reviewDeadline,
            int requiredReviewsPerPaper,
            int defaultReviewerCapacity,
            String status,
            long manuscriptCount,
            long reviewerCount,
            Instant createdAt
    ) {}

    public record TrackDto(
            UUID id,
            UUID conferenceId,
            String name,
            String description
    ) {}
}
