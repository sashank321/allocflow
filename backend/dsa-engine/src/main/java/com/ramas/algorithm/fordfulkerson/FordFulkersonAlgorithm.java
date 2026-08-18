package com.ramas.algorithm.fordfulkerson;

import com.ramas.algorithm.flow.*;

import java.util.*;

/**
 * Ford-Fulkerson algorithm using deterministic Depth-First Search (DFS)
 * to find augmenting paths in the residual graph.
 *
 * Theoretical Complexity: O(E * |f|), where |f| is maximum flow.
 */
public class FordFulkersonAlgorithm implements MaxFlowAlgorithm {

    public static final String NAME = "Ford-Fulkerson";
    public static final String COMPLEXITY = "O(E · |f|)";

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
        List<ExecutionTraceStep> traces = new ArrayList<>();

        while (true) {
            boolean[] visited = new boolean[vCount];
            FlowEdge[] edgeTo = new FlowEdge[vCount];

            // Run deterministic DFS to find an augmenting path from s to t
            boolean pathFound = dfs(network, s, t, visited, edgeTo);
            if (!pathFound) {
                break; // No augmenting path exists
            }

            // Find bottleneck capacity along the augmenting path
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
                    String.format("Augmenting path #%d found via DFS: +%d flow (Total: %d)", augmentations, bottleneck, totalFlow)
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
                augmentations,
                traces,
                Collections.emptyList(),
                minCutSourceNodes,
                saturatedAssignments,
                validConservation
        );
    }

    private boolean dfs(FlowNetwork network, int current, int target, boolean[] visited, FlowEdge[] edgeTo) {
        visited[current] = true;
        if (current == target) {
            return true;
        }

        // Iterate edges in deterministic natural order
        for (FlowEdge edge : network.adj(current)) {
            int next = edge.other(current);
            if (!visited[next] && edge.residualCapacityTo(next) > 0) {
                edgeTo[next] = edge;
                if (dfs(network, next, target, visited, edgeTo)) {
                    return true;
                }
            }
        }
        return false;
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

        // 1. Check edge capacities
        for (FlowEdge edge : network.getOriginalEdges()) {
            if (edge.getFlow() < 0 || edge.getFlow() > edge.getCapacity()) {
                return false;
            }
        }

        // 2. Check node conservation for all intermediate vertices
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
