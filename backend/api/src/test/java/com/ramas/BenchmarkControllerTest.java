package com.ramas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramas.dto.BenchmarkDtos.BenchmarkRequest;
import com.ramas.dto.BenchmarkDtos.ScalabilitySweepRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BenchmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("API Benchmark: Tri-algorithm comparison endpoint executes and returns FF, EK, Dinic metrics")
    void testTriAlgorithmComparisonEndpoint() throws Exception {
        BenchmarkRequest request = new BenchmarkRequest(
                15,
                10,
                2,
                4,
                0.35,
                0.05,
                6,
                482917L,
                1,
                3
        );

        mockMvc.perform(post("/api/v1/benchmarks/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.graphFingerprint").isNotEmpty())
                .andExpect(jsonPath("$.invariantSatisfied").value(true))
                .andExpect(jsonPath("$.algorithms").isArray())
                .andExpect(jsonPath("$.algorithms[0].algorithmName").value("Ford-Fulkerson"))
                .andExpect(jsonPath("$.algorithms[1].algorithmName").value("Edmonds-Karp"))
                .andExpect(jsonPath("$.algorithms[2].algorithmName").value("Dinic"));
    }

    @Test
    @DisplayName("API Benchmark: Scalability sweep returns empirical curve points with invariant verified")
    void testScalabilitySweepEndpoint() throws Exception {
        ScalabilitySweepRequest request = new ScalabilitySweepRequest(
                10,
                25,
                15,
                0.5,
                1,
                2,
                9999L
        );

        mockMvc.perform(post("/api/v1/benchmarks/scalability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allInvariantsVerified").value(true))
                .andExpect(jsonPath("$.points").isArray());
    }

    @Test
    @DisplayName("API Benchmark: History endpoint retrieves persisted experiment records")
    void testBenchmarkHistoryEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/benchmarks/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
