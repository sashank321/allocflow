package com.ramas.algorithm.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Executes multi-point parameter sweeps to analyze empirical scalability
 * (Graph Size |V| and |E| vs Runtime) across Ford-Fulkerson, Edmonds-Karp, and Dinic.
 */
public final class ScalabilityExperimentRunner {

    public record ScalabilityPoint(
            int manuscriptCount,
            int reviewerCount,
            int totalVertices,
            int totalEdges,
            long maxFlow,
            double fordFulkersonMedianMs,
            double edmondsKarpMedianMs,
            double dinicMedianMs,
            int fordFulkersonAugmentations,
            int edmondsKarpAugmentations,
            int dinicAugmentations,
            boolean invariantVerified
    ) {}

    public record ScalabilityReport(
            long seed,
            int startManuscripts,
            int endManuscripts,
            int stepSize,
            List<ScalabilityPoint> points,
            boolean allInvariantsVerified
    ) {}

    private ScalabilityExperimentRunner() {
    }

    public static ScalabilityReport runSweep(
            int startManuscripts,
            int endManuscripts,
            int stepSize,
            double reviewerRatio,
            int warmupTrials,
            int measuredTrials,
            long seed
    ) {
        int start = Math.max(5, startManuscripts);
        int end = Math.max(start, endManuscripts);
        int step = Math.max(5, stepSize);

        List<ScalabilityPoint> points = new ArrayList<>();
        boolean allVerified = true;

        for (int p = start; p <= end; p += step) {
            int r = Math.max(3, (int) Math.round(p * reviewerRatio));

            SyntheticDatasetConfig config = new SyntheticDatasetConfig(
                    p,
                    r,
                    2,
                    4,
                    0.35,
                    0.05,
                    8,
                    seed + p
            );

            SyntheticDatasetGenerator.GeneratedDataset dataset = SyntheticDatasetGenerator.generate(config);
            SequentialBenchmarkRunner.ComparisonReport comp = SequentialBenchmarkRunner.runComparison(
                    dataset.datasetId(),
                    dataset.buildResult().network(),
                    warmupTrials,
                    measuredTrials
            );

            BenchmarkResult ffRes = comp.algorithmResults().get(com.ramas.algorithm.fordfulkerson.FordFulkersonAlgorithm.NAME);
            BenchmarkResult ekRes = comp.algorithmResults().get(com.ramas.algorithm.edmondskarp.EdmondsKarpAlgorithm.NAME);
            BenchmarkResult dinicRes = comp.algorithmResults().get(com.ramas.algorithm.dinic.DinicAlgorithm.NAME);

            boolean pointValid = comp.invariantSatisfied();
            if (!pointValid) {
                allVerified = false;
            }

            points.add(new ScalabilityPoint(
                    p,
                    r,
                    comp.vertexCount(),
                    comp.edgeCount(),
                    comp.invariantMaxFlow(),
                    ffRes != null ? ffRes.medianDurationMs() : 0.0,
                    ekRes != null ? ekRes.medianDurationMs() : 0.0,
                    dinicRes != null ? dinicRes.medianDurationMs() : 0.0,
                    ffRes != null ? ffRes.augmentations() : 0,
                    ekRes != null ? ekRes.augmentations() : 0,
                    dinicRes != null ? dinicRes.augmentations() : 0,
                    pointValid
            ));
        }

        return new ScalabilityReport(
                seed,
                start,
                end,
                step,
                Collections.unmodifiableList(points),
                allVerified
        );
    }
}
