package com.ramas.controller;

import com.ramas.dto.ManuscriptDtos.*;
import com.ramas.service.ManuscriptService;
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
@RequestMapping("/api/v1/manuscripts")
@Tag(name = "Manuscripts", description = "Manuscript submission and tracking endpoints")
public class ManuscriptController {

    private final ManuscriptService manuscriptService;

    public ManuscriptController(ManuscriptService manuscriptService) {
        this.manuscriptService = manuscriptService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CONFERENCE_ADMIN', 'AUTHOR')")
    @Operation(summary = "Submit a new manuscript")
    public ResponseEntity<ManuscriptDto> createManuscript(
            @Valid @RequestBody ManuscriptRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(manuscriptService.createManuscript(request, userDetails.getUsername(), ip));
    }

    @GetMapping
    @Operation(summary = "List manuscripts (optionally filtered by conference)")
    public ResponseEntity<List<ManuscriptDto>> listManuscripts(@RequestParam(required = false) UUID conferenceId) {
        return ResponseEntity.ok(manuscriptService.listManuscripts(conferenceId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get manuscript by id")
    public ResponseEntity<ManuscriptDto> getManuscript(@PathVariable UUID id) {
        return ResponseEntity.ok(manuscriptService.getManuscript(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CONFERENCE_ADMIN')")
    @Operation(summary = "Update manuscript lifecycle status")
    public ResponseEntity<ManuscriptDto> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ManuscriptStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(manuscriptService.updateStatus(id, request.status(), userDetails.getUsername(), ip));
    }
}
