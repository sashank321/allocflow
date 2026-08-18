package com.ramas.dto;

import com.ramas.enums.ConflictType;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public class ConflictDtos {

    public record ConflictRequest(
            @NotNull UUID conferenceId,
            @NotNull UUID manuscriptId,
            @NotNull UUID reviewerId,
            @NotNull ConflictType conflictType,
            String reason
    ) {}

    public record ConflictDto(
            UUID id,
            UUID conferenceId,
            UUID manuscriptId,
            String manuscriptTitle,
            UUID reviewerId,
            String reviewerName,
            ConflictType conflictType,
            String reason,
            Instant createdAt
    ) {}
}
