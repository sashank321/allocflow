package com.ramas.algorithm.flow;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Computes a deterministic SHA-256 fingerprint for a FlowNetwork.
 * This guarantees mathematical proof that different algorithm runs
 * were evaluated on the EXACT same network structure and constraints.
 */
public final class GraphFingerprint {

    private GraphFingerprint() {
    }

    public static String compute(FlowNetwork network) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 1. Header: V, source, sink
            String header = String.format("V=%d;S=%d;T=%d\n",
                    network.getVertexCount(),
                    network.getSource(),
                    network.getSink());
            digest.update(header.getBytes(StandardCharsets.UTF_8));

            // 2. Vertex labels
            for (int v = 0; v < network.getVertexCount(); v++) {
                String vLabel = String.format("V[%d]=%s\n", v, network.getVertexLabel(v));
                digest.update(vLabel.getBytes(StandardCharsets.UTF_8));
            }

            // 3. Edges sorted deterministically by (from, to, capacity, manuscriptId, reviewerId)
            List<FlowEdge> sortedEdges = new ArrayList<>(network.getOriginalEdges());
            sortedEdges.sort(Comparator
                    .comparingInt(FlowEdge::getFrom)
                    .thenComparingInt(FlowEdge::getTo)
                    .thenComparingLong(FlowEdge::getCapacity)
                    .thenComparing(e -> e.getManuscriptId() != null ? e.getManuscriptId() : "")
                    .thenComparing(e -> e.getReviewerId() != null ? e.getReviewerId() : "")
            );

            for (FlowEdge edge : sortedEdges) {
                String edgeStr = String.format("E:%d->%d;cap=%d;type=%s;mid=%s;rid=%s\n",
                        edge.getFrom(),
                        edge.getTo(),
                        edge.getCapacity(),
                        edge.getEdgeType(),
                        edge.getManuscriptId() != null ? edge.getManuscriptId() : "",
                        edge.getReviewerId() != null ? edge.getReviewerId() : "");
                digest.update(edgeStr.getBytes(StandardCharsets.UTF_8));
            }

            byte[] hashBytes = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
