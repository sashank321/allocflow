package com.ramas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramas.dto.MatchingDtos.CommitRequest;
import com.ramas.dto.MatchingDtos.SimulationRequest;
import com.ramas.entity.Conference;
import com.ramas.enums.AlgorithmType;
import com.ramas.repository.ConferenceRepository;
import com.ramas.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MatchingWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("End-to-End: Simulate Dinic max-flow, preview graph, commit assignments, and verify explainability")
    void testEndToEndMatchingLifecycle() throws Exception {
        Conference conf = conferenceRepository.findAll().get(0);
        String adminToken = jwtTokenProvider.generateTokenForEmailAndRole("admin@allocflow.io", "SUPER_ADMIN");

        // 1. Simulate allocation using Dinic
        SimulationRequest simRequest = new SimulationRequest(
                conf.getId(),
                AlgorithmType.DINIC,
                2,
                4,
                true
        );

        MvcResult simResult = mockMvc.perform(post("/api/v1/matching/simulate")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(simRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.runId").isNotEmpty())
                .andExpect(jsonPath("$.algorithmName").value("Dinic"))
                .andExpect(jsonPath("$.graphFingerprint").isNotEmpty())
                .andExpect(jsonPath("$.achievedFlow").isNumber())
                .andExpect(jsonPath("$.validation.valid").value(true))
                .andExpect(jsonPath("$.assignments").isArray())
                .andExpect(jsonPath("$.graphVisualization.nodes").isArray())
                .andExpect(jsonPath("$.graphVisualization.edges").isArray())
                .andReturn();

        String responseJson = simResult.getResponse().getContentAsString();
        String runIdStr = objectMapper.readTree(responseJson).get("runId").asText();
        UUID runId = UUID.fromString(runIdStr);

        // 2. Commit the simulated run transactionally
        CommitRequest commitReq = new CommitRequest("Approved by Conference Chair");
        mockMvc.perform(post("/api/v1/matching/commit/" + runId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commitReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.runId").value(runIdStr))
                .andExpect(jsonPath("$.status").value("COMMITTED"))
                .andExpect(jsonPath("$.committedAssignmentsCount").isNumber());

        // 3. Explain an assignment
        String mIdStr = objectMapper.readTree(responseJson).get("assignments").get(0).get("manuscriptId").asText();
        String rIdStr = objectMapper.readTree(responseJson).get("assignments").get(0).get("reviewerId").asText();

        mockMvc.perform(get("/api/v1/matching/explain")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("manuscriptId", mIdStr)
                        .param("reviewerId", rIdStr)
                        .param("runId", runIdStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.manuscriptId").value(mIdStr))
                .andExpect(jsonPath("$.reviewerId").value(rIdStr))
                .andExpect(jsonPath("$.conflictFree").value(true))
                .andExpect(jsonPath("$.explanationSummary").isNotEmpty());
    }
}
