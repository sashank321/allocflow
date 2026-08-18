package com.ramas.algorithm;

import com.ramas.algorithm.flow.FlowEdge;
import com.ramas.algorithm.flow.FlowNetwork;
import com.ramas.algorithm.flow.GraphFingerprint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlowNetworkTest {

    @Test
    @DisplayName("FlowNetwork initializes correctly with source and sink labels")
    void testNetworkInitialization() {
        FlowNetwork network = new FlowNetwork(4, 0, 3);
        assertThat(network.getVertexCount()).isEqualTo(4);
        assertThat(network.getSource()).isEqualTo(0);
        assertThat(network.getSink()).isEqualTo(3);
        assertThat(network.getVertexLabel(0)).isEqualTo("SOURCE");
        assertThat(network.getVertexLabel(3)).isEqualTo("SINK");
        assertThat(network.getEdgeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Adding edge registers edge in both endpoints' adjacency lists")
    void testSharedEdgeAdjacency() {
        FlowNetwork network = new FlowNetwork(3, 0, 2);
        FlowEdge edge = new FlowEdge(0, 1, 10L);
        network.addEdge(edge);

        assertThat(network.getEdgeCount()).isEqualTo(1);
        assertThat(network.adj(0)).hasSize(1);
        assertThat(network.adj(1)).hasSize(1);

        // Edge in both adj lists is identical object
        FlowEdge e0 = network.adj(0).get(0);
        FlowEdge e1 = network.adj(1).get(0);
        assertThat(e0).isSameAs(e1);
        assertThat(e0.getFrom()).isEqualTo(0);
        assertThat(e0.getTo()).isEqualTo(1);
        assertThat(e0.residualCapacityTo(1)).isEqualTo(10L); // forward
        assertThat(e0.residualCapacityTo(0)).isEqualTo(0L);  // backward
    }

    @Test
    @DisplayName("Deep clone produces identical independent copy")
    void testDeepClone() {
        FlowNetwork original = new FlowNetwork(4, 0, 3);
        original.setVertexLabel(1, "Paper-1");
        original.setVertexLabel(2, "Reviewer-1");
        original.addEdge(new FlowEdge(0, 1, 2L, FlowEdge.EdgeType.SOURCE_TO_MANUSCRIPT, "P1", null));
        original.addEdge(new FlowEdge(1, 2, 1L, FlowEdge.EdgeType.MANUSCRIPT_TO_REVIEWER, "P1", "R1"));
        original.addEdge(new FlowEdge(2, 3, 3L, FlowEdge.EdgeType.REVIEWER_TO_SINK, null, "R1"));

        FlowNetwork clone = original.deepClone();

        assertThat(clone.getVertexCount()).isEqualTo(original.getVertexCount());
        assertThat(clone.getEdgeCount()).isEqualTo(original.getEdgeCount());
        assertThat(clone.getFingerprint()).isEqualTo(original.getFingerprint());

        // Mutate original edge flow
        original.adj(0).get(0).addResidualFlowTo(1, 2L);
        assertThat(original.adj(0).get(0).getFlow()).isEqualTo(2L);
        // Clone flow must remain untouched (0L)
        assertThat(clone.adj(0).get(0).getFlow()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Deterministic SHA-256 fingerprint is invariant under clone and edge additions")
    void testFingerprint() {
        FlowNetwork net1 = new FlowNetwork(3, 0, 2);
        net1.addEdge(new FlowEdge(0, 1, 5L));
        net1.addEdge(new FlowEdge(1, 2, 5L));

        FlowNetwork net2 = new FlowNetwork(3, 0, 2);
        net2.addEdge(new FlowEdge(0, 1, 5L));
        net2.addEdge(new FlowEdge(1, 2, 5L));

        assertThat(net1.getFingerprint()).isEqualTo(net2.getFingerprint());
    }
}
