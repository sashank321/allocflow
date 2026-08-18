package com.ramas.service;

import com.ramas.algorithm.evaluation.*;
import com.ramas.dto.BenchmarkDtos.*;
import com.ramas.entity.ExperimentRecord;
import com.ramas.enums.AuditAction;
import com.ramas.repository.ExperimentRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BenchmarkService {

    private final ExperimentRecordRepository experimentRecordRepository;
    private final AuditService auditService;

    public BenchmarkService(ExperimentRecordRepository experimentRecordRepository, AuditService auditService) {
        this.experimentRecordRepository = experimentRecordRepository;
        this.auditService = auditService;
    }

    @Transactional
    public BenchmarkComparisonResponse runTriAlgorithmComparison(BenchmarkRequest request, String actorEmail, String ipAddress) {
        int manuscripts = request.manuscriptCount() > 0 ? request.manuscriptCount() : 30;
        int reviewers = request.reviewerCount() > 0 ? request.reviewerCount() : 15;
        int reqReviews = request.requiredReviewsPerPaper() != null && request.requiredReviewsPerPaper() > 0 ? request.requiredReviewsPerPaper() : 2;
        int capacity = request.reviewerCapacity() != null && request.reviewerCapacity() > 0 ? request.reviewerCapacity() : 4;
        double eligibility = request.eligibilityProbability() != null ? request.eligibilityProbability() : 0.35;
        double conflict = request.conflictProbability() != null ? request.conflictProbability() : 0.05;
        int topics = request.topicCount() != null ? request.topicCount() : 8;
        long seed = request.randomSeed() != null ? request.randomSeed() : 482917L;
        int warmups = request.warmupTrials() != null ? request.warmupTrials() : 3;
        int trials = request.measuredTrials() != null ? request.measuredTrials() : 10;

        SyntheticDatasetConfig config = new SyntheticDatasetConfig(
                manuscripts,
                reviewers,
                reqReviews,
                capacity,
                eligibility,
                conflict,
                topics,
                seed
        );

        SyntheticDatasetGenerator.GeneratedDataset dataset = SyntheticDatasetGenerator.generate(config);
        SequentialBenchmarkRunner.ComparisonReport compReport = SequentialBenchmarkRunner.runComparison(
                dataset.datasetId(),
                dataset.buildResult().network(),
                warmups,
                trials
        );

        List<AlgorithmMetricDto> algorithmDtos = new ArrayList<>();
        Map<String, List<String>> algorithmTraces = new LinkedHashMap<>();

        for (String algoName : compReport.algorithmOrder()) {
            BenchmarkResult res = compReport.algorithmResults().get(algoName);
            if (res != null) {
                algorithmDtos.add(new AlgorithmMetricDto(
                        res.algorithmName(),
                        res.theoreticalComplexity(),
                        res.graphFingerprint(),
                        res.maxFlow(),
                        res.warmupTrials(),
                        res.measuredTrials(),
                        res.minDurationMs(),
                        res.medianDurationMs(),
                        res.p95DurationMs(),
                        res.maxDurationMs(),
                        res.meanDurationMs(),
                        res.stdDevDurationNs() / 1_000_000.0,
                        res.augmentations(),
                        res.phases(),
                        res.validityStatus(),
                        res.invariantVerified()
                ));

                List<String> traceSummaries = res.maxFlowResult().traces().stream()
                        .limit(15)
                        .map(t -> String.format("Iter %d: %s | Δ=%d | Cum=%d",
                                t.iteration(), t.pathDescription(), t.bottleneckCapacity(), t.cumulativeFlow()))
                        .toList();
                algorithmTraces.put(algoName, traceSummaries);
            }
        }

        BenchmarkResult ff = compReport.algorithmResults().get("Ford-Fulkerson");
        BenchmarkResult ek = compReport.algorithmResults().get("Edmonds-Karp");
        BenchmarkResult dinic = compReport.algorithmResults().get("Dinic");

        ExperimentRecord record = new ExperimentRecord(
                dataset.datasetId(),
                seed,
                manuscripts,
                reviewers,
                compReport.vertexCount(),
                compReport.edgeCount(),
                compReport.graphFingerprint(),
                compReport.invariantMaxFlow(),
                ff != null ? ff.medianDurationMs() : 0.0,
                ek != null ? ek.medianDurationMs() : 0.0,
                dinic != null ? dinic.medianDurationMs() : 0.0,
                ff != null ? ff.augmentations() : 0,
                ek != null ? ek.augmentations() : 0,
                dinic != null ? dinic.augmentations() : 0,
                compReport.invariantSatisfied()
        );
        experimentRecordRepository.save(record);

        auditService.log(
                actorEmail != null ? actorEmail : "RESEARCH_LAB",
                AuditAction.EXPERIMENT_RUN,
                "EXPERIMENT",
                record.getId().toString(),
                String.format("Benchmark [%s]: V=%d, E=%d, Flow=%d, FF=%.2fms, EK=%.2fms, Dinic=%.2fms (Invariant: %s)",
                        dataset.datasetId(), compReport.vertexCount(), compReport.edgeCount(), compReport.invariantMaxFlow(),
                        ff != null ? ff.medianDurationMs() : 0.0, ek != null ? ek.medianDurationMs() : 0.0, dinic != null ? dinic.medianDurationMs() : 0.0,
                        compReport.invariantSatisfied() ? "SATISFIED" : "VIOLATED"),
                ipAddress
        );

        return new BenchmarkComparisonResponse(
                dataset.datasetId(),
                compReport.graphFingerprint(),
                compReport.vertexCount(),
                compReport.edgeCount(),
                compReport.totalSourceCapacity(),
                compReport.totalSinkCapacity(),
                compReport.invariantSatisfied(),
                compReport.invariantMaxFlow(),
                algorithmDtos,
                algorithmTraces
        );
    }

    @Transactional(readOnly = true)
    public ScalabilitySweepResponse runScalabilitySweep(ScalabilitySweepRequest request) {
        int start = request.startManuscripts() != null ? request.startManuscripts() : 10;
        int end = request.endManuscripts() != null ? request.endManuscripts() : 100;
        int step = request.stepSize() != null ? request.stepSize() : 20;
        double ratio = request.reviewerRatio() != null ? request.reviewerRatio() : 0.40;
        int warmups = request.warmupTrials() != null ? request.warmupTrials() : 2;
        int trials = request.measuredTrials() != null ? request.measuredTrials() : 5;
        long seed = request.seed() != null ? request.seed() : 482917L;

        ScalabilityExperimentRunner.ScalabilityReport report = ScalabilityExperimentRunner.runSweep(
                start, end, step, ratio, warmups, trials, seed
        );

        List<ScalabilityPointDto> pointDtos = report.points().stream().map(pt -> new ScalabilityPointDto(
                pt.manuscriptCount(),
                pt.reviewerCount(),
                pt.totalVertices(),
                pt.totalEdges(),
                pt.maxFlow(),
                pt.fordFulkersonMedianMs(),
                pt.edmondsKarpMedianMs(),
                pt.dinicMedianMs(),
                pt.fordFulkersonAugmentations(),
                pt.edmondsKarpAugmentations(),
                pt.dinicAugmentations(),
                pt.invariantVerified()
        )).toList();

        return new ScalabilitySweepResponse(
                report.seed(),
                report.startManuscripts(),
                report.endManuscripts(),
                report.stepSize(),
                pointDtos,
                report.allInvariantsVerified()
        );
    }

    @Transactional(readOnly = true)
    public List<ExperimentRecord> getRecentExperiments() {
        return experimentRecordRepository.findTop20ByOrderByCreatedAtDesc();
    }
}
