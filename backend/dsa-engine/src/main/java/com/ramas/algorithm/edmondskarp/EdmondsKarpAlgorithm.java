package com.ramas.algorithm.edmondskarp;

import com.ramas.algorithm.flow.*;

import java.util.*;

/**
 * Edmonds-Karp algorithm using Breadth-First Search (BFS)
 * to find the shortest augmenting paths in the residual graph.
 *
 * Theoretical Complexity: O(V * E^2).
 */
public class EdmondsKarpAlgorithm implements MaxFlowAlgorithm {

    public static final String NAME = "Edmonds-Karp";
    public static final String COMPLEXITY = "O(V · E²)";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getTheoreticalComplexity() {
        return COMPLEXITY;
    }

    @Override
    public MaxFlowResult solve(FlowNetwork network) {
        int s = network.getSource();
        int t = network.getSink();
        int vCount = network.getVertexCount();
        String fingerprint = network.getFingerprint();

        long totalFlow = 0;
        int augmentations = 0;
        int bfsRounds = 0;
        List<ExecutionTraceStep> traces = new ArrayList<>();

        FlowEdge[] edgeTo = new FlowEdge[vCount];

        while (hasAugmentingPath(network, s, t, edgeTo)) {
            bfsRounds++;

            // Find bottleneck capacity along the shortest augmenting path found by BFS
            long bottleneck = Long.MAX_VALUE;
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                bottleneck = Math.min(bottleneck, edgeTo[v].residualCapacityTo(v));
            }

            // Augment flow along the path
            List<Integer> pathNodes = new ArrayList<>();
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                edgeTo[v].addResidualFlowTo(v, bottleneck);
                pathNodes.add(v);
            }
            pathNodes.add(s);
            Collections.reverse(pathNodes);

            totalFlow += bottleneck;
            augmentations++;

            // Record execution trace step
            List<String> nodeLabels = new ArrayList<>();
            StringBuilder pathDesc = new StringBuilder();
            for (int i = 0; i < pathNodes.size(); i++) {
                int node = pathNodes.get(i);
                String label = network.getVertexLabel(node);
                nodeLabels.add(label);
                pathDesc.append(label);
                if (i < pathNodes.size() - 1) {
                    pathDesc.append(" -> ");
                }
            }

            traces.add(new ExecutionTraceStep(
                    augmentations,
                    pathDesc.toString(),
                    pathNodes,
                    nodeLabels,
                    bottleneck,
                    totalFlow,
                    String.format("BFS Shortest Augmenting Path #%d: +%d flow (Total: %d)", augmentations, bottleneck, totalFlow)
            ));
        }

        // Identify min-cut vertices reachable from source in residual graph
        Set<Integer> minCutSourceNodes = computeResidualReachableNodes(network, s);

        // Collect saturated manuscript-reviewer assignment edges
        List<FlowEdge> saturatedAssignments = new ArrayList<>();
        for (FlowEdge edge : network.getOriginalEdges()) {
            if (edge.getEdgeType() == FlowEdge.EdgeType.MANUSCRIPT_TO_REVIEWER && edge.getFlow() > 0) {
                saturatedAssignments.add(edge);
            }
        }

        boolean validConservation = verifyFlowConservation(network, totalFlow);

        return new MaxFlowResult(
                NAME,
                fingerprint,
                totalFlow,
                augmentations,
                bfsRounds,
                traces,
                Collections.emptyList(),
                minCutSourceNodes,
                saturatedAssignments,
                validConservation
        );
    }

    private boolean hasAugmentingPath(FlowNetwork network, int s, int t, FlowEdge[] edgeTo) {
        Arrays.fill(edgeTo, null);
        boolean[] marked = new boolean[network.getVertexCount()];
        Queue<Integer> queue = new ArrayDeque<>();

        marked[s] = true;
        queue.add(s);

        while (!queue.isEmpty() && !marked[t]) {
            int v = queue.poll();

            for (FlowEdge edge : network.adj(v)) {
                int w = edge.other(v);

                if (edge.residualCapacityTo(w) > 0 && !marked[w]) {
                    edgeTo[w] = edge;
                    marked[w] = true;
                    queue.add(w);
                }
            }
        }

        return marked[t];
    }

    private Set<Integer> computeResidualReachableNodes(FlowNetwork network, int s) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new ArrayDeque<>();
        visited.add(s);
        queue.add(s);

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            for (FlowEdge edge : network.adj(curr)) {
                int next = edge.other(curr);
                if (!visited.contains(next) && edge.residualCapacityTo(next) > 0) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return visited;
    }

    private boolean verifyFlowConservation(FlowNetwork network, long expectedTotalFlow) {
        int s = network.getSource();
        int t = network.getSink();

        for (FlowEdge edge : network.getOriginalEdges()) {
            if (edge.getFlow() < 0 || edge.getFlow() > edge.getCapacity()) {
                return false;
            }
        }

        for (int v = 0; v < network.getVertexCount(); v++) {
            if (v == s || v == t) continue;

            long inFlow = 0;
            long outFlow = 0;
            for (FlowEdge edge : network.adj(v)) {
                if (edge.getFrom() == v) {
                    outFlow += edge.getFlow();
                } else if (edge.getTo() == v) {
                    inFlow += edge.getFlow();
                }
            }
            if (inFlow != outFlow) {
                return false;
            }
        }
        return true;
    }
}
