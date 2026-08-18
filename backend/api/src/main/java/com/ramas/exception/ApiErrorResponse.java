package com.ramas.exception;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String requestId,
        List<String> details
) {
    public static ApiErrorResponse of(int status, String error, String message, String path, List<String> details) {
        return new ApiErrorResponse(
                Instant.now(),
                status,
                error,
                message,
                path,
                java.util.UUID.randomUUID().toString().substring(0, 8),
                details != null ? details : List.of()
        );
    }
}
