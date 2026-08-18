package com.ramas.algorithm;

import com.ramas.algorithm.allocation.*;
import com.ramas.algorithm.dinic.DinicAlgorithm;
import com.ramas.algorithm.edmondskarp.EdmondsKarpAlgorithm;
import com.ramas.algorithm.evaluation.SyntheticDatasetConfig;
import com.ramas.algorithm.evaluation.SyntheticDatasetGenerator;
import com.ramas.algorithm.flow.FlowEdge;
import com.ramas.algorithm.flow.FlowNetwork;
import com.ramas.algorithm.flow.MaxFlowResult;
import com.ramas.algorithm.fordfulkerson.FordFulkersonAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MaxFlowEquivalenceTest {

    private FordFulkersonAlgorithm fordFulkerson;
    private EdmondsKarpAlgorithm edmondsKarp;
    private DinicAlgorithm dinic;

    @BeforeEach
    void setUp() {
        fordFulkerson = new FordFulkersonAlgorithm();
        edmondsKarp = new EdmondsKarpAlgorithm();
        dinic = new DinicAlgorithm();
    }

    @Test
    @DisplayName("Boundary Case: Disconnected graph with no intermediate edges yields 0 flow")
    void testDisconnectedGraph() {
        FlowNetwork network = new FlowNetwork(4, 0, 3);
        // S -> P1, but P1 is not connected to R1, and R1 -> T
        network.addEdge(new FlowEdge(0, 1, 2L));
        network.addEdge(new FlowEdge(2, 3, 2L));

        MaxFlowResult resFF = fordFulkerson.solve(network.deepClone());
        MaxFlowResult resEK = edmondsKarp.solve(network.deepClone());
        MaxFlowResult resDinic = dinic.solve(network.deepClone());

        assertThat(resFF.maxFlow()).isZero();
        assertThat(resEK.maxFlow()).isZero();
        assertThat(resDinic.maxFlow()).isZero();
    }

    @Test
    @DisplayName("Single Pair: 1 Manuscript, 1 Reviewer yields flow 1")
    void testSinglePair() {
        FlowNetwork network = new FlowNetwork(4, 0, 3);
        network.addEdge(new FlowEdge(0, 1, 1L, FlowEdge.EdgeType.SOURCE_TO_MANUSCRIPT, "P1", null));
        network.addEdge(new FlowEdge(1, 2, 1L, FlowEdge.EdgeType.MANUSCRIPT_TO_REVIEWER, "P1", "R1"));
        network.addEdge(new FlowEdge(2, 3, 1L, FlowEdge.EdgeType.REVIEWER_TO_SINK, null, "R1"));

        MaxFlowResult resFF = fordFulkerson.solve(network.deepClone());
        MaxFlowResult resEK = edmondsKarp.solve(network.deepClone());
        MaxFlowResult resDinic = dinic.solve(network.deepClone());

        assertThat(resFF.maxFlow()).isEqualTo(1L);
        assertThat(resEK.maxFlow()).isEqualTo(1L);
        assertThat(resDinic.maxFlow()).isEqualTo(1L);

        assertThat(resFF.flowConservationValid()).isTrue();
        assertThat(resEK.flowConservationValid()).isTrue();
        assertThat(resDinic.flowConservationValid()).isTrue();
    }

    @Test
    @DisplayName("Classic Diamond Bottleneck Graph: All algorithms find max flow = 200")
    void testClassicDiamondBottleneck() {
        // 0 (S), 1 (A), 2 (B), 3 (T)
        // S->A: 100, S->B: 100, A->B: 1, A->T: 100, B->T: 100
        FlowNetwork network = new FlowNetwork(4, 0, 3);
        network.addEdge(new FlowEdge(0, 1, 100L));
        network.addEdge(new FlowEdge(0, 2, 100L));
        network.addEdge(new FlowEdge(1, 2, 1L));
        network.addEdge(new FlowEdge(1, 3, 100L));
        network.addEdge(new FlowEdge(2, 3, 100L));

        MaxFlowResult resFF = fordFulkerson.solve(network.deepClone());
        MaxFlowResult resEK = edmondsKarp.solve(network.deepClone());
        MaxFlowResult resDinic = dinic.solve(network.deepClone());

        assertThat(resFF.maxFlow()).isEqualTo(200L);
        assertThat(resEK.maxFlow()).isEqualTo(200L);
        assertThat(resDinic.maxFlow()).isEqualTo(200L);
    }

    @Test
    @DisplayName("Conflict Exclusion: Manuscript P1 cannot be assigned to Reviewer R1 when conflict declared")
    void testConflictExclusion() {
        ManuscriptNode p1 = new ManuscriptNode("P1", "Neural Routing", "ML", 1, Set.of("ML"), Set.of("flow"), Set.of("A1"), Set.of("MIT"));
        ReviewerNode r1 = new ReviewerNode("R1", "Dr. Alice", "alice@mit.edu", "MIT", 2, true, true, Set.of("ML"), Set.of("flow"));
        ReviewerNode r2 = new ReviewerNode("R2", "Dr. Bob", "bob@cmu.edu", "CMU", 2, true, true, Set.of("ML"), Set.of("flow"));

        // Institutional conflict between P1 and R1 (both MIT)
        BipartiteGraphBuilder.BuildResult buildRes = BipartiteGraphBuilder.buildNetwork(
                List.of(p1),
                List.of(r1, r2),
                List.of(),
                1
        );

        // Network should only have edge P1 -> R2 (R1 excluded due to institutional conflict)
        assertThat(buildRes.conflictExclusionsCount()).isEqualTo(1);
        assertThat(buildRes.eligibleEdgesCount()).isEqualTo(1);

        FlowNetwork netClone = buildRes.network().deepClone();
        MaxFlowResult resDinic = dinic.solve(netClone);

        assertThat(resDinic.maxFlow()).isEqualTo(1L);
        List<AssignmentExtractor.AssignedPair> assignments = AssignmentExtractor.extractAssignments(netClone);
        assertThat(assignments).hasSize(1);
        assertThat(assignments.get(0).reviewerId()).isEqualTo("R2");
    }

    @ParameterizedTest(name = "Equivalence on Random Graph with Seed {0}")
    @ValueSource(longs = {42L, 101L, 482917L, 999999L, 1234567L})
    @DisplayName("Research Invariant: FF.maxFlow == EK.maxFlow == Dinic.maxFlow on seeded graphs")
    void testMaxFlowEquivalenceOnRandomGraphs(long seed) {
        SyntheticDatasetConfig config = new SyntheticDatasetConfig(
                25,   // 25 manuscripts
                15,   // 15 reviewers
                2,    // 2 reviews each
                4,    // capacity 4
                0.40, // 40% eligibility
                0.05, // 5% conflicts
                6,    // 6 topics
                seed
        );

        SyntheticDatasetGenerator.GeneratedDataset dataset = SyntheticDatasetGenerator.generate(config);
        FlowNetwork canonical = dataset.buildResult().network();

        MaxFlowResult resFF = fordFulkerson.solve(canonical.deepClone());
        MaxFlowResult resEK = edmondsKarp.solve(canonical.deepClone());
        MaxFlowResult resDinic = dinic.solve(canonical.deepClone());

        // Assert core invariant
        assertThat(resFF.maxFlow())
                .as("Ford-Fulkerson maxFlow must equal Dinic maxFlow")
                .isEqualTo(resDinic.maxFlow());

        assertThat(resEK.maxFlow())
                .as("Edmonds-Karp maxFlow must equal Dinic maxFlow")
                .isEqualTo(resDinic.maxFlow());

        // Assert flow conservation
        assertThat(resFF.flowConservationValid()).isTrue();
        assertThat(resEK.flowConservationValid()).isTrue();
        assertThat(resDinic.flowConservationValid()).isTrue();

        // Validate extracted assignments from solved Dinic graph
        FlowNetwork solvedClone = canonical.deepClone();
        dinic.solve(solvedClone);
        List<AssignmentExtractor.AssignedPair> assignments = AssignmentExtractor.extractAssignments(solvedClone);

        ValidationResult valResult = AssignmentValidator.validate(
                assignments,
                dataset.manuscripts(),
                dataset.reviewers(),
                dataset.conflicts(),
                config.requiredReviewsPerPaper()
        );

        assertThat(valResult.valid())
                .as("Extracted assignments must pass all sanity constraints")
                .isTrue();
        assertThat(valResult.validationErrors()).isEmpty();
    }
}
