package com.ramas.algorithm.dinic;

import com.ramas.algorithm.flow.*;

import java.util.*;

/**
 * Dinic's Algorithm using BFS Level Graphs and DFS Blocking Flow with Work Pointers.
 *
 * Theoretical Complexity: O(V² · E), and O(E · √V) on unit networks / bipartite matching.
 */
public class DinicAlgorithm implements MaxFlowAlgorithm {

    public static final String NAME = "Dinic";
    public static final String COMPLEXITY = "O(V² · E)";

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
        int totalAugmentations = 0;
        int phaseCount = 0;

        int[] level = new int[vCount];
        int[] ptr = new int[vCount];

        List<ExecutionTraceStep> allTraces = new ArrayList<>();
        List<PhaseTrace> phaseTraces = new ArrayList<>();

        // Loop over BFS phases until sink is unreachable in the level graph
        while (bfsLevelGraph(network, s, t, level)) {
            phaseCount++;
            Arrays.fill(ptr, 0); // Reset work pointers for DFS

            long phaseFlow = 0;
            List<ExecutionTraceStep> phaseSteps = new ArrayList<>();

            // Capture level graph snapshot for trace visualization
            Map<String, Integer> levelMap = new LinkedHashMap<>();
            for (int v = 0; v < vCount; v++) {
                if (level[v] != -1) {
                    levelMap.put(network.getVertexLabel(v), level[v]);
                }
            }

            // Find blocking flow in the level graph using DFS
            while (true) {
                List<Integer> path = new ArrayList<>();
                path.add(s);
                long pushed = sendDfsFlow(network, s, t, Long.MAX_VALUE, level, ptr, path);
                if (pushed == 0) {
                    break;
                }

                totalFlow += pushed;
                phaseFlow += pushed;
                totalAugmentations++;

                // Format trace step
                List<String> nodeLabels = new ArrayList<>();
                StringBuilder pathDesc = new StringBuilder();
                for (int i = 0; i < path.size(); i++) {
                    int node = path.get(i);
                    String label = network.getVertexLabel(node);
                    nodeLabels.add(label);
                    pathDesc.append(label);
                    if (i < path.size() - 1) {
                        pathDesc.append(" -> ");
                    }
                }

                ExecutionTraceStep step = new ExecutionTraceStep(
                        totalAugmentations,
                        pathDesc.toString(),
                        path,
                        nodeLabels,
                        pushed,
                        totalFlow,
                        String.format("Phase %d Blocking Path #%d: +%d flow (Total: %d)", phaseCount, totalAugmentations, pushed, totalFlow)
                );
                phaseSteps.add(step);
                allTraces.add(step);
            }

            phaseTraces.add(new PhaseTrace(
                    phaseCount,
                    levelMap,
                    phaseSteps,
                    phaseFlow,
                    totalFlow,
                    String.format("Phase %d complete: %d blocking paths found, +%d flow (Cumulative: %d)",
                            phaseCount, phaseSteps.size(), phaseFlow, totalFlow)
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
                totalAugmentations,
                phaseCount,
                allTraces,
                phaseTraces,
                minCutSourceNodes,
                saturatedAssignments,
                validConservation
        );
    }

    private boolean bfsLevelGraph(FlowNetwork network, int s, int t, int[] level) {
        Arrays.fill(level, -1);
        level[s] = 0;

        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(s);

        while (!queue.isEmpty()) {
            int v = queue.poll();

            for (FlowEdge edge : network.adj(v)) {
                int w = edge.other(v);

                if (edge.residualCapacityTo(w) > 0 && level[w] == -1) {
                    level[w] = level[v] + 1;
                    queue.add(w);
                }
            }
        }

        return level[t] != -1;
    }

    private long sendDfsFlow(FlowNetwork network, int u, int t, long pushed, int[] level, int[] ptr, List<Integer> currentPath) {
        if (pushed == 0) return 0;
        if (u == t) return pushed;

        List<FlowEdge> edges = network.adj(u);

        for (int cid = ptr[u]; cid < edges.size(); cid = ++ptr[u]) {
            FlowEdge edge = edges.get(cid);
            int v = edge.other(u);

            // Forward in level graph and has residual capacity
            if (level[v] == level[u] + 1 && edge.residualCapacityTo(v) > 0) {
                currentPath.add(v);
                long tr = sendDfsFlow(network, v, t, Math.min(pushed, edge.residualCapacityTo(v)), level, ptr, currentPath);
                if (tr > 0) {
                    edge.addResidualFlowTo(v, tr);
                    return tr;
                }
                // Backtrack if no flow pushed along this sub-path
                currentPath.remove(currentPath.size() - 1);
            }
        }

        return 0;
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
