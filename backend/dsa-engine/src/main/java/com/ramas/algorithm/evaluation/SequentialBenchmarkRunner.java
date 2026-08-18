package com.ramas.algorithm.evaluation;

import com.ramas.algorithm.dinic.DinicAlgorithm;
import com.ramas.algorithm.edmondskarp.EdmondsKarpAlgorithm;
import com.ramas.algorithm.flow.FlowNetwork;
import com.ramas.algorithm.flow.MaxFlowAlgorithm;
import com.ramas.algorithm.flow.MaxFlowResult;
import com.ramas.algorithm.fordfulkerson.FordFulkersonAlgorithm;

import java.util.*;

/**
 * Sequential benchmark execution engine.
 * Solves the canonical network using Ford-Fulkerson, Edmonds-Karp, and Dinic
 * sequentially (never in parallel) on isolated deep clones to avoid CPU contention.
 *
 * Enforces and verifies the fundamental research invariant:
 * MaxFlow(Ford-Fulkerson) == MaxFlow(Edmonds-Karp) == MaxFlow(Dinic)
 */
public final class SequentialBenchmarkRunner {

    public record ComparisonReport(
            String datasetId,
            String graphFingerprint,
            int vertexCount,
            int edgeCount,
            long totalSourceCapacity,
            long totalSinkCapacity,
            boolean invariantSatisfied,
            long invariantMaxFlow,
            Map<String, BenchmarkResult> algorithmResults,
            List<String> algorithmOrder
    ) {}

    private SequentialBenchmarkRunner() {
    }

    public static ComparisonReport runComparison(
            String datasetId,
            FlowNetwork canonicalNetwork,
            int warmupTrials,
            int measuredTrials
    ) {
        List<MaxFlowAlgorithm> algorithms = List.of(
                new FordFulkersonAlgorithm(),
                new EdmondsKarpAlgorithm(),
                new DinicAlgorithm()
        );
        return runComparison(datasetId, canonicalNetwork, algorithms, warmupTrials, measuredTrials);
    }

    public static ComparisonReport runComparison(
            String datasetId,
            FlowNetwork canonicalNetwork,
            List<MaxFlowAlgorithm> algorithms,
            int warmupTrials,
            int measuredTrials
    ) {
        String canonicalFingerprint = canonicalNetwork.getFingerprint();
        int warmups = Math.max(1, warmupTrials);
        int trials = Math.max(1, measuredTrials);

        Map<String, BenchmarkResult> resultsMap = new LinkedHashMap<>();
        List<String> algorithmOrder = new ArrayList<>();
        Map<String, MaxFlowResult> lastResults = new HashMap<>();

        // Run each algorithm sequentially
        for (MaxFlowAlgorithm algo : algorithms) {
            String algoName = algo.getName();
            algorithmOrder.add(algoName);

            // 1. Warmup trials on isolated clones
            for (int w = 0; w < warmups; w++) {
                FlowNetwork warmupClone = canonicalNetwork.deepClone();
                algo.solve(warmupClone);
            }

            // 2. Measured trials on isolated clones
            long[] durationsNs = new long[trials];
            MaxFlowResult sampleResult = null;

            for (int t = 0; t < trials; t++) {
                FlowNetwork trialClone = canonicalNetwork.deepClone();

                long start = System.nanoTime();
                MaxFlowResult res = algo.solve(trialClone);
                long end = System.nanoTime();

                durationsNs[t] = Math.max(1L, end - start);
                if (sampleResult == null) {
                    sampleResult = res;
                }
            }

            lastResults.put(algoName, sampleResult);

            BenchmarkResult benchmarkResult = BenchmarkResult.compute(
                    algoName,
                    algo.getTheoreticalComplexity(),
                    canonicalFingerprint,
                    durationsNs,
                    warmups,
                    sampleResult,
                    true // Will be checked globally below
            );

            resultsMap.put(algoName, benchmarkResult);
        }

        // 3. Verify fundamental research invariant: all algorithms must produce EQUIVALENT max flow
        long expectedFlow = -1;
        boolean invariantSatisfied = true;

        for (Map.Entry<String, MaxFlowResult> entry : lastResults.entrySet()) {
            long currentFlow = entry.getValue().maxFlow();
            if (expectedFlow == -1) {
                expectedFlow = currentFlow;
            } else if (expectedFlow != currentFlow) {
                invariantSatisfied = false;
                break;
            }
        }

        // Update invariant flag on results if mismatched
        if (!invariantSatisfied) {
            for (String name : algorithmOrder) {
                BenchmarkResult old = resultsMap.get(name);
                resultsMap.put(name, new BenchmarkResult(
                        old.algorithmName(), old.theoreticalComplexity(), old.graphFingerprint(),
                        old.maxFlow(), old.warmupTrials(), old.measuredTrials(),
                        old.minDurationNs(), old.medianDurationNs(), old.p95DurationNs(), old.maxDurationNs(),
                        old.meanDurationNs(), old.stdDevDurationNs(),
                        old.minDurationMs(), old.medianDurationMs(), old.p95DurationMs(), old.maxDurationMs(),
                        old.meanDurationMs(), old.augmentations(), old.phases(),
                        old.validityStatus(), false, old.maxFlowResult()
                ));
            }
        }

        return new ComparisonReport(
                datasetId != null ? datasetId : "EXP-" + canonicalFingerprint.substring(0, 8),
                canonicalFingerprint,
                canonicalNetwork.getVertexCount(),
                canonicalNetwork.getEdgeCount(),
                canonicalNetwork.getTotalSourceCapacity(),
                canonicalNetwork.getTotalSinkCapacity(),
                invariantSatisfied,
                expectedFlow,
                Collections.unmodifiableMap(resultsMap),
                Collections.unmodifiableList(algorithmOrder)
        );
    }
}
