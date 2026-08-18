package com.ramas.algorithm.allocation;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Pure domain representation of a manuscript in the allocation graph.
 */
public record ManuscriptNode(
        String id,
        String title,
        String track,
        int requiredReviews,
        Set<String> topics,
        Set<String> keywords,
        Set<String> authorIds,
        Set<String> authorAffiliations
) {
    public ManuscriptNode {
        topics = topics != null ? Set.copyOf(topics) : Collections.emptySet();
        keywords = keywords != null ? Set.copyOf(keywords) : Collections.emptySet();
        authorIds = authorIds != null ? Set.copyOf(authorIds) : Collections.emptySet();
        authorAffiliations = authorAffiliations != null ? Set.copyOf(authorAffiliations) : Collections.emptySet();
    }
}
