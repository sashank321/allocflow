package com.ramas.algorithm;

import com.ramas.algorithm.evaluation.*;
import com.ramas.algorithm.flow.FlowNetwork;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SequentialBenchmarkRunnerTest {

    @Test
    @DisplayName("SequentialBenchmarkRunner executes all algorithms with consistent fingerprints and satisfies invariants")
    void testBenchmarkExecution() {
        SyntheticDatasetConfig config = SyntheticDatasetConfig.defaultConfig(15, 10, 482917L);
        SyntheticDatasetGenerator.GeneratedDataset dataset = SyntheticDatasetGenerator.generate(config);
        FlowNetwork canonical = dataset.buildResult().network();

        SequentialBenchmarkRunner.ComparisonReport report = SequentialBenchmarkRunner.runComparison(
                dataset.datasetId(),
                canonical,
                2, // 2 warmups
                5  // 5 measured trials
        );

        assertThat(report.invariantSatisfied()).isTrue();
        assertThat(report.graphFingerprint()).isEqualTo(canonical.getFingerprint());
        assertThat(report.algorithmOrder()).containsExactly("Ford-Fulkerson", "Edmonds-Karp", "Dinic");

        for (String algo : report.algorithmOrder()) {
            BenchmarkResult res = report.algorithmResults().get(algo);
            assertThat(res).isNotNull();
            assertThat(res.graphFingerprint()).isEqualTo(report.graphFingerprint());
            assertThat(res.maxFlow()).isEqualTo(report.invariantMaxFlow());
            assertThat(res.minDurationNs()).isGreaterThan(0);
            assertThat(res.medianDurationNs()).isGreaterThanOrEqualTo(res.minDurationNs());
            assertThat(res.maxDurationNs()).isGreaterThanOrEqualTo(res.medianDurationNs());
            assertThat(res.invariantVerified()).isTrue();
        }
    }

    @Test
    @DisplayName("Scalability sweep generates valid data points for multiple graph sizes")
    void testScalabilitySweep() {
        ScalabilityExperimentRunner.ScalabilityReport sweep = ScalabilityExperimentRunner.runSweep(
                10,
                30,
                10,
                0.5,
                1,
                3,
                12345L
        );

        assertThat(sweep.allInvariantsVerified()).isTrue();
        assertThat(sweep.points()).hasSize(3); // 10, 20, 30
        for (ScalabilityExperimentRunner.ScalabilityPoint pt : sweep.points()) {
            assertThat(pt.invariantVerified()).isTrue();
            assertThat(pt.totalVertices()).isGreaterThan(0);
            assertThat(pt.totalEdges()).isGreaterThan(0);
        }
    }
}
