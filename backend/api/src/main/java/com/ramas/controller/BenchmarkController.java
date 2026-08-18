package com.ramas.controller;

import com.ramas.dto.BenchmarkDtos.*;
import com.ramas.entity.ExperimentRecord;
import com.ramas.service.BenchmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/benchmarks")
@Tag(name = "Benchmarks & Research", description = "Empirical Max-Flow algorithm comparison and scalability parameter sweep laboratory")
public class BenchmarkController {

    private final BenchmarkService benchmarkService;

    public BenchmarkController(BenchmarkService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }

    @PostMapping("/compare")
    @Operation(summary = "Run reproducible tri-algorithm comparison (FF vs EK vs Dinic) on a canonical synthetic graph")
    public ResponseEntity<BenchmarkComparisonResponse> compare(
            @RequestBody BenchmarkRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest
    ) {
        String email = userDetails != null ? userDetails.getUsername() : "RESEARCH_LAB";
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(benchmarkService.runTriAlgorithmComparison(request, email, ip));
    }

    @PostMapping("/scalability")
    @Operation(summary = "Run configurable scalability sweep across multiple graph sizes and plot empirical curves")
    public ResponseEntity<ScalabilitySweepResponse> scalability(
            @RequestBody ScalabilitySweepRequest request
    ) {
        return ResponseEntity.ok(benchmarkService.runScalabilitySweep(request));
    }

    @GetMapping("/history")
    @Operation(summary = "Retrieve recent benchmark experiment history")
    public ResponseEntity<List<ExperimentRecord>> getHistory() {
        return ResponseEntity.ok(benchmarkService.getRecentExperiments());
    }
}
