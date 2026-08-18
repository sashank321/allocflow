package com.ramas.algorithm.allocation;

/**
 * Representation of a declared or system-detected Conflict of Interest (COI).
 */
public record ConflictDeclaration(
        String manuscriptId,
        String reviewerId,
        String conflictType,
        String reason
) {
}
