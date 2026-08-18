package com.ramas.algorithm.flow;

import java.util.List;
import java.util.Map;

/**
 * Encapsulates a phase in Dinic's algorithm:
 * - BFS level graph assignment (node index/label -> level)
 * - DFS blocking flow augmenting paths found in this phase
 * - Phase flow and cumulative flow
 */
public record PhaseTrace(
        int phaseNumber,
        Map<String, Integer> levelGraph,
        List<ExecutionTraceStep> blockingFlowSteps,
        long phaseFlow,
        long cumulativeFlow,
        String summary
) {
    public PhaseTrace {
        if (levelGraph != null) {
            levelGraph = Map.copyOf(levelGraph);
        }
        if (blockingFlowSteps != null) {
            blockingFlowSteps = List.copyOf(blockingFlowSteps);
        }
    }
}
