package com.ramas.algorithm.allocation;

import java.util.Collections;
import java.util.Set;

/**
 * Pure domain representation of a reviewer in the allocation graph.
 */
public record ReviewerNode(
        String id,
        String name,
        String email,
        String affiliation,
        int maxCapacity,
        boolean active,
        boolean available,
        Set<String> topics,
        Set<String> keywords
) {
    public ReviewerNode {
        topics = topics != null ? Set.copyOf(topics) : Collections.emptySet();
        keywords = keywords != null ? Set.copyOf(keywords) : Collections.emptySet();
    }
}
