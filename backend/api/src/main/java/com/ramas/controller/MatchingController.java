package com.ramas.controller;

import com.ramas.dto.ExplanationDto.AssignmentExplanationDto;
import com.ramas.dto.MatchingDtos.*;
import com.ramas.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/matching")
@Tag(name = "Matching Engine", description = "Bipartite graph max-flow allocation, preview, commit, and explainability endpoints")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @PostMapping("/simulate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CONFERENCE_ADMIN')")
    @Operation(summary = "Build bipartite flow graph and simulate max-flow allocation (non-destructive preview)")
    public ResponseEntity<SimulationResponse> simulate(
            @Valid @RequestBody SimulationRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(matchingService.simulateAllocation(request, userDetails.getUsername(), ip));
    }

    @PostMapping("/commit/{runId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CONFERENCE_ADMIN')")
    @Operation(summary = "Transactionally commit a simulated assignment run to the database")
    public ResponseEntity<CommitResponse> commit(
            @PathVariable UUID runId,
            @RequestBody(required = false) CommitRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(matchingService.commitAllocation(runId, request != null ? request : new CommitRequest(null), userDetails.getUsername(), ip));
    }

    @PostMapping("/override")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CONFERENCE_ADMIN')")
    @Operation(summary = "Manually override/create an assignment with audit logging")
    public ResponseEntity<OverrideResponse> override(
            @Valid @RequestBody OverrideRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(matchingService.overrideAssignment(request, userDetails.getUsername(), ip));
    }

    @GetMapping("/explain")
    @Operation(summary = "Explain this assignment: Return structured reasoning for a manuscript-reviewer edge")
    public ResponseEntity<AssignmentExplanationDto> explain(
            @RequestParam UUID manuscriptId,
            @RequestParam UUID reviewerId,
            @RequestParam(required = false) UUID runId
    ) {
        return ResponseEntity.ok(matchingService.explainAssignment(manuscriptId, reviewerId, runId));
    }
}
