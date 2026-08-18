package com.ramas.algorithm.evaluation;

import com.ramas.algorithm.flow.MaxFlowResult;

/**
 * Encapsulates the statistical benchmark timings and algorithmic performance
 * of an algorithm evaluated over repeated, sequential trials on a cloned canonical graph.
 */
public record BenchmarkResult(
        String algorithmName,
        String theoreticalComplexity,
        String graphFingerprint,
        long maxFlow,
        int warmupTrials,
        int measuredTrials,
        long minDurationNs,
        long medianDurationNs,
        long p95DurationNs,
        long maxDurationNs,
        double meanDurationNs,
        double stdDevDurationNs,
        double minDurationMs,
        double medianDurationMs,
        double p95DurationMs,
        double maxDurationMs,
        double meanDurationMs,
        int augmentations,
        int phases,
        String validityStatus,
        boolean invariantVerified,
        MaxFlowResult maxFlowResult
) {
    public static BenchmarkResult compute(
            String algorithmName,
            String complexity,
            String graphFingerprint,
            long[] durationsNs,
            int warmupTrials,
            MaxFlowResult result,
            boolean invariantVerified
    ) {
        long[] sorted = durationsNs.clone();
        java.util.Arrays.sort(sorted);

        int n = sorted.length;
        long min = sorted[0];
        long max = sorted[n - 1];
        long median = (n % 2 == 0) ? (sorted[n / 2 - 1] + sorted[n / 2]) / 2 : sorted[n / 2];
        int p95Index = Math.min(n - 1, (int) Math.ceil(0.95 * n) - 1);
        long p95 = sorted[p95Index];

        double sum = 0;
        for (long d : sorted) sum += d;
        double mean = sum / n;

        double variance = 0;
        for (long d : sorted) variance += Math.pow(d - mean, 2);
        double stdDev = Math.sqrt(variance / n);

        return new BenchmarkResult(
                algorithmName,
                complexity,
                graphFingerprint,
                result.maxFlow(),
                warmupTrials,
                n,
                min,
                median,
                p95,
                max,
                mean,
                stdDev,
                min / 1_000_000.0,
                median / 1_000_000.0,
                p95 / 1_000_000.0,
                max / 1_000_000.0,
                mean / 1_000_000.0,
                result.augmentationsCount(),
                result.phasesCount(),
                result.flowConservationValid() ? "VALID" : "INVALID",
                invariantVerified,
                result
        );
    }
}
