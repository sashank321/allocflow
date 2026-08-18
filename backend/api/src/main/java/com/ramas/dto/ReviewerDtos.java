package com.ramas.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class ReviewerDtos {

    public record ReviewerRequest(
            @NotNull UUID userId,
            @NotNull UUID conferenceId,
            String affiliation,
            int maxCapacity,
            boolean active,
            boolean available,
            Set<String> topics,
            Set<String> keywords
    ) {}

    public record ReviewerDto(
            UUID id,
            UUID userId,
            String userName,
            String userEmail,
            UUID conferenceId,
            String conferenceCode,
            String affiliation,
            int maxCapacity,
            int currentWorkload,
            boolean active,
            boolean available,
            Set<String> topics,
            Set<String> keywords,
            Instant createdAt
    ) {}
}
