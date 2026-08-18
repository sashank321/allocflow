package com.ramas.algorithm.flow;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Encapsulates the algorithmic solution output of a maximum flow algorithm.
 * Note: Timing benchmarks are separated in BenchmarkResult to ensure research purity.
 */
public record MaxFlowResult(
        String algorithmName,
        String graphFingerprint,
        long maxFlow,
        int augmentationsCount,
        int phasesCount,
        List<ExecutionTraceStep> traces,
        List<PhaseTrace> phaseTraces,
        Set<Integer> sourceSideCutNodes,
        List<FlowEdge> saturatedAssignmentEdges,
        boolean flowConservationValid
) {
    public MaxFlowResult {
        if (traces != null) {
            traces = List.copyOf(traces);
        } else {
            traces = Collections.emptyList();
        }
        if (phaseTraces != null) {
            phaseTraces = List.copyOf(phaseTraces);
        } else {
            phaseTraces = Collections.emptyList();
        }
        if (sourceSideCutNodes != null) {
            sourceSideCutNodes = Set.copyOf(sourceSideCutNodes);
        } else {
            sourceSideCutNodes = Collections.emptySet();
        }
        if (saturatedAssignmentEdges != null) {
            saturatedAssignmentEdges = List.copyOf(saturatedAssignmentEdges);
        } else {
            saturatedAssignmentEdges = Collections.emptyList();
        }
    }
}
