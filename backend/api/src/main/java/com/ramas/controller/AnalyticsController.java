package com.ramas.controller;

import com.ramas.dto.DashboardStatsDto;
import com.ramas.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics", description = "Operations and conference analytics endpoints")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get aggregated conference operations analytics")
    public ResponseEntity<DashboardStatsDto> getDashboardStats(@RequestParam(required = false) UUID conferenceId) {
        return ResponseEntity.ok(analyticsService.getDashboardStats(conferenceId));
    }
}
