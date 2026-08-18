package com.ramas.algorithm.flow;

import java.util.List;

/**
 * Encapsulates a single step/iteration in an augmenting-path max-flow algorithm execution.
 * Useful for interactive step-by-step visual replay and pedagogical explanations.
 */
public record ExecutionTraceStep(
        int iteration,
        String pathDescription,
        List<Integer> nodeIndices,
        List<String> nodeLabels,
        long bottleneckCapacity,
        long cumulativeFlow,
        String message
) {
    public ExecutionTraceStep {
        if (nodeIndices != null) {
            nodeIndices = List.copyOf(nodeIndices);
        }
        if (nodeLabels != null) {
            nodeLabels = List.copyOf(nodeLabels);
        }
    }
}
