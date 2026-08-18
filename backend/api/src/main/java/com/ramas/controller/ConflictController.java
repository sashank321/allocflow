package com.ramas.controller;

import com.ramas.dto.ConflictDtos.*;
import com.ramas.service.ConflictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/conflicts")
@Tag(name = "Conflicts", description = "Conflict of Interest (COI) management endpoints")
public class ConflictController {

    private final ConflictService conflictService;

    public ConflictController(ConflictService conflictService) {
        this.conflictService = conflictService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CONFERENCE_ADMIN', 'REVIEWER', 'AUTHOR')")
    @Operation(summary = "Declare a Conflict of Interest")
    public ResponseEntity<ConflictDto> createConflict(
            @Valid @RequestBody ConflictRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(conflictService.createConflict(request, userDetails.getUsername(), ip));
    }

    @GetMapping
    @Operation(summary = "List declared conflicts")
    public ResponseEntity<List<ConflictDto>> listConflicts(@RequestParam(required = false) UUID conferenceId) {
        return ResponseEntity.ok(conflictService.listConflicts(conferenceId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CONFERENCE_ADMIN')")
    @Operation(summary = "Delete/revoke a conflict declaration")
    public ResponseEntity<Void> deleteConflict(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        conflictService.deleteConflict(id, userDetails.getUsername(), ip);
        return ResponseEntity.noContent().build();
    }
}
