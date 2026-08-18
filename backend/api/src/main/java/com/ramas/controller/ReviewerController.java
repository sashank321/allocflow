package com.ramas.controller;

import com.ramas.dto.ReviewerDtos.*;
import com.ramas.service.ReviewerService;
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
@RequestMapping("/api/v1/reviewers")
@Tag(name = "Reviewers", description = "Reviewer management and profile endpoints")
public class ReviewerController {

    private final ReviewerService reviewerService;

    public ReviewerController(ReviewerService reviewerService) {
        this.reviewerService = reviewerService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CONFERENCE_ADMIN')")
    @Operation(summary = "Register a reviewer for a conference")
    public ResponseEntity<ReviewerDto> createReviewer(
            @Valid @RequestBody ReviewerRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(reviewerService.createReviewer(request, userDetails.getUsername(), ip));
    }

    @GetMapping
    @Operation(summary = "List reviewers (optionally filtered by conference)")
    public ResponseEntity<List<ReviewerDto>> listReviewers(@RequestParam(required = false) UUID conferenceId) {
        return ResponseEntity.ok(reviewerService.listReviewers(conferenceId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reviewer details by id")
    public ResponseEntity<ReviewerDto> getReviewer(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewerService.getReviewer(id));
    }
}
