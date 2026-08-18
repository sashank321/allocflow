package com.ramas.algorithm.evaluation;

import com.ramas.algorithm.allocation.BipartiteGraphBuilder;
import com.ramas.algorithm.allocation.ConflictDeclaration;
import com.ramas.algorithm.allocation.ManuscriptNode;
import com.ramas.algorithm.allocation.ReviewerNode;

import java.util.*;

/**
 * Generates deterministic synthetic conference datasets using a seeded pseudo-random number generator.
 * Guarantees reproducibility across independent experiment runs.
 */
public final class SyntheticDatasetGenerator {

    private static final String[] TOPIC_POOL = {
            "Machine Learning",
            "Distributed Systems",
            "Graph Algorithms",
            "Cybersecurity",
            "Database Systems",
            "Software Engineering",
            "Human-Computer Interaction",
            "Computer Vision",
            "Natural Language Processing",
            "Quantum Computing",
            "Cloud Infrastructure",
            "Network Protocols"
    };

    private static final String[] KEYWORD_POOL = {
            "optimization", "flow", "consensus", "cryptography", "indexing",
            "neural", "concurrency", "privacy", "replication", "heuristic",
            "transformers", "fault-tolerance", "scheduling", "routing", "bipartite"
    };

    private static final String[] AFFILIATIONS = {
            "MIT", "Stanford University", "CMU", "UC Berkeley", "Oxford",
            "Cambridge", "ETH Zurich", "NUS", "Tsinghua University", "IIT Madras"
    };

    private SyntheticDatasetGenerator() {
    }

    public record GeneratedDataset(
            SyntheticDatasetConfig config,
            List<ManuscriptNode> manuscripts,
            List<ReviewerNode> reviewers,
            List<ConflictDeclaration> conflicts,
            BipartiteGraphBuilder.BuildResult buildResult,
            String datasetId
    ) {}

    public static GeneratedDataset generate(SyntheticDatasetConfig config) {
        Random rng = new Random(config.randomSeed());

        int numTopics = Math.min(config.topicCount(), TOPIC_POOL.length);
        List<String> activeTopics = Arrays.asList(Arrays.copyOf(TOPIC_POOL, numTopics));

        // 1. Generate Manuscripts
        List<ManuscriptNode> manuscripts = new ArrayList<>();
        for (int i = 1; i <= config.manuscriptCount(); i++) {
            String mId = "P" + i;
            String title = String.format("Manuscript %d: On %s Architectures", i, activeTopics.get(rng.nextInt(numTopics)));
            String track = activeTopics.get(rng.nextInt(numTopics));

            Set<String> mTopics = new HashSet<>();
            int tCount = 1 + rng.nextInt(2); // 1-2 topics
            for (int t = 0; t < tCount; t++) {
                mTopics.add(activeTopics.get(rng.nextInt(numTopics)));
            }

            Set<String> mKeywords = new HashSet<>();
            int kCount = 1 + rng.nextInt(3); // 1-3 keywords
            for (int k = 0; k < kCount; k++) {
                mKeywords.add(KEYWORD_POOL[rng.nextInt(KEYWORD_POOL.length)]);
            }

            Set<String> authorAffils = new HashSet<>();
            authorAffils.add(AFFILIATIONS[rng.nextInt(AFFILIATIONS.length)]);

            Set<String> authorIds = new HashSet<>();
            authorIds.add("AUTH_" + i);

            manuscripts.add(new ManuscriptNode(
                    mId,
                    title,
                    track,
                    config.requiredReviewsPerPaper(),
                    mTopics,
                    mKeywords,
                    authorIds,
                    authorAffils
            ));
        }

        // 2. Generate Reviewers
        List<ReviewerNode> reviewers = new ArrayList<>();
        for (int j = 1; j <= config.reviewerCount(); j++) {
            String rId = "R" + j;
            String name = String.format("Dr. Reviewer %d", j);
            String email = String.format("reviewer%d@institution.edu", j);
            String affil = AFFILIATIONS[rng.nextInt(AFFILIATIONS.length)];

            Set<String> rTopics = new HashSet<>();
            int tCount = 1 + rng.nextInt(3); // 1-3 topics
            for (int t = 0; t < tCount; t++) {
                rTopics.add(activeTopics.get(rng.nextInt(numTopics)));
            }

            Set<String> rKeywords = new HashSet<>();
            int kCount = 2 + rng.nextInt(3); // 2-4 keywords
            for (int k = 0; k < kCount; k++) {
                rKeywords.add(KEYWORD_POOL[rng.nextInt(KEYWORD_POOL.length)]);
            }

            reviewers.add(new ReviewerNode(
                    rId,
                    name,
                    email,
                    affil,
                    config.reviewerCapacity(),
                    true,  // active
                    true,  // available
                    rTopics,
                    rKeywords
            ));
        }

        // 3. Generate Random Conflicts
        List<ConflictDeclaration> conflicts = new ArrayList<>();
        for (ManuscriptNode m : manuscripts) {
            for (ReviewerNode r : reviewers) {
                if (rng.nextDouble() < config.conflictProbability()) {
                    conflicts.add(new ConflictDeclaration(
                            m.id(),
                            r.id(),
                            "PERSONAL",
                            "Declared collaborative or personal conflict"
                    ));
                }
            }
        }

        // 4. Build canonical Bipartite Graph
        BipartiteGraphBuilder.BuildResult buildResult = BipartiteGraphBuilder.buildNetwork(
                manuscripts,
                reviewers,
                conflicts,
                config.requiredReviewsPerPaper()
        );

        String datasetId = String.format("EXP-%d-%dx%d", config.randomSeed(), config.manuscriptCount(), config.reviewerCount());

        return new GeneratedDataset(
                config,
                Collections.unmodifiableList(manuscripts),
                Collections.unmodifiableList(reviewers),
                Collections.unmodifiableList(conflicts),
                buildResult,
                datasetId
        );
    }
}
