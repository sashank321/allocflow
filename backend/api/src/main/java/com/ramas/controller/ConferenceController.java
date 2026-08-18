package com.ramas.controller;

import com.ramas.dto.ConferenceDtos.*;
import com.ramas.service.ConferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/conferences")
@Tag(name = "Conferences", description = "Multi-conference management endpoints")
public class ConferenceController {

    private final ConferenceService conferenceService;

    public ConferenceController(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CONFERENCE_ADMIN')")
    @Operation(summary = "Create a new conference")
    public ResponseEntity<ConferenceDto> createConference(@Valid @RequestBody ConferenceRequest request) {
        return ResponseEntity.ok(conferenceService.createConference(request));
    }

    @GetMapping
    @Operation(summary = "List all conferences")
    public ResponseEntity<List<ConferenceDto>> listConferences() {
        return ResponseEntity.ok(conferenceService.listConferences());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get conference details by id")
    public ResponseEntity<ConferenceDto> getConference(@PathVariable UUID id) {
        return ResponseEntity.ok(conferenceService.getConference(id));
    }

    @GetMapping("/{id}/tracks")
    @Operation(summary = "Get tracks for a conference")
    public ResponseEntity<List<TrackDto>> getTracks(@PathVariable UUID id) {
        return ResponseEntity.ok(conferenceService.getTracks(id));
    }
}
