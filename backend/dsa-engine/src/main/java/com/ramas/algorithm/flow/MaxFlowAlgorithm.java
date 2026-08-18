package com.ramas.algorithm.flow;

/**
 * Universal interface implemented by all maximum flow algorithms.
 * Every algorithm must solve the given network, mutating its residual edges
 * to establish optimal flow, while producing detailed invariant verification
 * and execution trace steps.
 */
public interface MaxFlowAlgorithm {

    /**
     * Solves the maximum flow problem on the provided flow network.
     *
     * @param network the network to solve (typically a cloned instance)
     * @return MaxFlowResult containing total flow, augmentation metrics, traces, and cut nodes
     */
    MaxFlowResult solve(FlowNetwork network);

    /**
     * Returns the formal name of the algorithm.
     */
    String getName();

    /**
     * Returns the formal asymptotic worst-case complexity string.
     */
    String getTheoreticalComplexity();
}
