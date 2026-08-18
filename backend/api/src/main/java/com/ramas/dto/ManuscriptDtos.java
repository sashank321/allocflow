package com.ramas.dto;

import com.ramas.enums.ManuscriptStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class ManuscriptDtos {

    public record ManuscriptRequest(
            @NotNull UUID conferenceId,
            UUID trackId,
            @NotBlank String title,
            String abstractText,
            int requiredReviews,
            Set<String> topics,
            Set<String> keywords,
            Set<String> authorAffiliations
    ) {}

    public record ManuscriptDto(
            UUID id,
            UUID conferenceId,
            String conferenceCode,
            UUID trackId,
            String trackName,
            UUID authorId,
            String authorName,
            String authorEmail,
            String title,
            String abstractText,
            ManuscriptStatus status,
            int requiredReviews,
            Set<String> topics,
            Set<String> keywords,
            Set<String> authorAffiliations,
            Instant createdAt,
            Instant updatedAt
    ) {}

    public record ManuscriptStatusRequest(
            @NotNull ManuscriptStatus status
    ) {}
}
