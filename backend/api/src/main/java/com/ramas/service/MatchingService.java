package com.ramas.service;

import com.ramas.algorithm.allocation.*;
import com.ramas.algorithm.dinic.DinicAlgorithm;
import com.ramas.algorithm.edmondskarp.EdmondsKarpAlgorithm;
import com.ramas.algorithm.flow.FlowEdge;
import com.ramas.algorithm.flow.FlowNetwork;
import com.ramas.algorithm.flow.MaxFlowAlgorithm;
import com.ramas.algorithm.flow.MaxFlowResult;
import com.ramas.algorithm.fordfulkerson.FordFulkersonAlgorithm;
import com.ramas.dto.ExplanationDto.AssignmentExplanationDto;
import com.ramas.dto.MatchingDtos.*;
import com.ramas.entity.*;
import com.ramas.enums.AlgorithmType;
import com.ramas.enums.AssignmentRunStatus;
import com.ramas.enums.AuditAction;
import com.ramas.enums.ManuscriptStatus;
import com.ramas.exception.BadRequestException;
import com.ramas.exception.ResourceNotFoundException;
import com.ramas.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class MatchingService {

    private final ConferenceRepository conferenceRepository;
    private final ManuscriptRepository manuscriptRepository;
    private final ReviewerRepository reviewerRepository;
    private final ConflictOfInterestRepository conflictRepository;
    private final AssignmentRunRepository assignmentRunRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public MatchingService(ConferenceRepository conferenceRepository,
                           ManuscriptRepository manuscriptRepository,
                           ReviewerRepository reviewerRepository,
                           ConflictOfInterestRepository conflictRepository,
                           AssignmentRunRepository assignmentRunRepository,
                           AssignmentRepository assignmentRepository,
                           UserRepository userRepository,
                           AuditService auditService) {
        this.conferenceRepository = conferenceRepository;
        this.manuscriptRepository = manuscriptRepository;
        this.reviewerRepository = reviewerRepository;
        this.conflictRepository = conflictRepository;
        this.assignmentRunRepository = assignmentRunRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional
    public SimulationResponse simulateAllocation(SimulationRequest request, String actorEmail, String ipAddress) {
        Conference conference = conferenceRepository.findById(request.conferenceId())
                .orElseThrow(() -> new ResourceNotFoundException("Conference not found with id: " + request.conferenceId()));

        List<Manuscript> manuscripts = manuscriptRepository.findByConference(conference);
        List<Reviewer> reviewers = reviewerRepository.findByConferenceAndActiveTrue(conference);
        List<ConflictOfInterest> conflicts = Boolean.FALSE.equals(request.excludeConflicts())
                ? Collections.emptyList()
                : conflictRepository.findByConference(conference);

        if (manuscripts.isEmpty()) {
            throw new BadRequestException("No manuscripts available for assignment in this conference");
        }
        if (reviewers.isEmpty()) {
            throw new BadRequestException("No active reviewers available in this conference");
        }

        // Convert JPA entities to pure domain nodes for dsa-engine
        List<ManuscriptNode> mNodes = new ArrayList<>();
        Map<String, Manuscript> mEntityMap = new HashMap<>();
        int reqPerPaper = request.requiredReviewsPerPaper() != null ? request.requiredReviewsPerPaper() : conference.getRequiredReviewsPerPaper();

        for (Manuscript m : manuscripts) {
            String mId = m.getId().toString();
            mEntityMap.put(mId, m);
            mNodes.add(new ManuscriptNode(
                    mId,
                    m.getTitle(),
                    m.getTrack() != null ? m.getTrack().getName() : "General",
                    m.getRequiredReviews() > 0 ? m.getRequiredReviews() : reqPerPaper,
                    m.getTopics(),
                    m.getKeywords(),
                    Set.of(m.getAuthor().getId().toString()),
                    m.getAuthorAffiliations()
            ));
        }

        List<ReviewerNode> rNodes = new ArrayList<>();
        Map<String, Reviewer> rEntityMap = new HashMap<>();
        int defaultCap = request.defaultReviewerCapacity() != null ? request.defaultReviewerCapacity() : conference.getDefaultReviewerCapacity();

        for (Reviewer r : reviewers) {
            String rId = r.getId().toString();
            rEntityMap.put(rId, r);
            rNodes.add(new ReviewerNode(
                    rId,
                    r.getUser().getFullName(),
                    r.getUser().getEmail(),
                    r.getAffiliation(),
                    r.getMaxCapacity() > 0 ? r.getMaxCapacity() : defaultCap,
                    r.isActive(),
                    r.isAvailable(),
                    r.getTopics(),
                    r.getKeywords()
            ));
        }

        List<ConflictDeclaration> cNodes = new ArrayList<>();
        for (ConflictOfInterest c : conflicts) {
            cNodes.add(new ConflictDeclaration(
                    c.getManuscript().getId().toString(),
                    c.getReviewer().getId().toString(),
                    c.getConflictType().name(),
                    c.getReason()
            ));
        }

        // 1. Build canonical Bipartite Flow Network (S -> P -> R -> T)
        BipartiteGraphBuilder.BuildResult buildResult = BipartiteGraphBuilder.buildNetwork(
                mNodes,
                rNodes,
                cNodes,
                reqPerPaper
        );

        FlowNetwork canonicalNetwork = buildResult.network();
        String fingerprint = canonicalNetwork.getFingerprint();

        // 2. Select Max Flow Algorithm
        MaxFlowAlgorithm algo = switch (request.algorithm()) {
            case FORD_FULKERSON -> new FordFulkersonAlgorithm();
            case EDMONDS_KARP -> new EdmondsKarpAlgorithm();
            case DINIC -> new DinicAlgorithm();
        };

        // 3. Solve on isolated deep clone
        FlowNetwork executionClone = canonicalNetwork.deepClone();
        long startNs = System.nanoTime();
        MaxFlowResult flowResult = algo.solve(executionClone);
        long endNs = System.nanoTime();
        double durationMs = (endNs - startNs) / 1_000_000.0;

        // 4. Extract assigned pairs
        List<AssignmentExtractor.AssignedPair> assignedPairs = AssignmentExtractor.extractAssignments(executionClone);

        // 5. Validate assignment against domain rules
        ValidationResult valResult = AssignmentValidator.validate(
                assignedPairs,
                mNodes,
                rNodes,
                cNodes,
                reqPerPaper
        );

        // 6. Persist AssignmentRun (SIMULATED state)
        AssignmentRun run = new AssignmentRun(
                conference,
                request.algorithm(),
                fingerprint,
                mNodes.size(),
                rNodes.size(),
                canonicalNetwork.getVertexCount(),
                canonicalNetwork.getEdgeCount(),
                buildResult.totalRequiredReviews(),
                flowResult.maxFlow(),
                valResult.coveragePercentage(),
                durationMs,
                flowResult.augmentationsCount(),
                flowResult.phasesCount()
        );
        AssignmentRun savedRun = assignmentRunRepository.save(run);

        // 7. Format assigned pair DTOs
        List<AssignedPairDto> assignmentDtos = new ArrayList<>();
        for (AssignmentExtractor.AssignedPair pair : assignedPairs) {
            Manuscript m = mEntityMap.get(pair.manuscriptId());
            Reviewer r = rEntityMap.get(pair.reviewerId());

            ManuscriptNode mn = mNodes.stream().filter(n -> n.id().equals(pair.manuscriptId())).findFirst().orElse(null);
            ReviewerNode rn = rNodes.stream().filter(n -> n.id().equals(pair.reviewerId())).findFirst().orElse(null);
            CompatibilityCalculator.CompatibilityDetails compat = (mn != null && rn != null)
                    ? CompatibilityCalculator.evaluate(mn, rn)
                    : new CompatibilityCalculator.CompatibilityDetails(true, 0, 0, 1.0, Set.of(), Set.of(), "");

            assignmentDtos.add(new AssignedPairDto(
                    m.getId(),
                    m.getTitle(),
                    r.getId(),
                    r.getUser().getFullName(),
                    r.getAffiliation(),
                    pair.flow(),
                    compat.score(),
                    compat.topicOverlapCount(),
                    compat.keywordOverlapCount()
            ));
        }

        // 8. Generate Graph Visualization DTO
        GraphVisualizationDto graphVis = buildGraphVisualization(executionClone, buildResult, mEntityMap, rEntityMap);

        // 9. Format execution trace summary
        List<String> traceSummaries = flowResult.traces().stream()
                .limit(20)
                .map(t -> String.format("Iter %d: %s | Bottleneck: %d | Total Flow: %d",
                        t.iteration(), t.pathDescription(), t.bottleneckCapacity(), t.cumulativeFlow()))
                .toList();

        auditService.log(actorEmail, AuditAction.ASSIGNMENT_RUN_COMPLETED, "ASSIGNMENT_RUN", savedRun.getId().toString(),
                String.format("Simulated %s run: flow=%d/%d (%.1f%% coverage)",
                        algo.getName(), flowResult.maxFlow(), buildResult.totalRequiredReviews(), valResult.coveragePercentage()),
                ipAddress);

        ValidationSummaryDto valSummary = new ValidationSummaryDto(
                valResult.valid(),
                valResult.totalAssignedPairs(),
                valResult.totalRequiredReviews(),
                valResult.coveragePercentage(),
                valResult.fullySatisfiedManuscripts(),
                valResult.partiallySatisfiedManuscripts(),
                valResult.zeroReviewManuscripts(),
                valResult.validationErrors(),
                valResult.warnings()
        );

        return new SimulationResponse(
                savedRun.getId(),
                conference.getId(),
                conference.getCode(),
                request.algorithm(),
                algo.getName(),
                algo.getTheoreticalComplexity(),
                fingerprint,
                mNodes.size(),
                rNodes.size(),
                canonicalNetwork.getVertexCount(),
                canonicalNetwork.getEdgeCount(),
                buildResult.totalRequiredReviews(),
                flowResult.maxFlow(),
                valResult.coveragePercentage(),
                durationMs,
                flowResult.augmentationsCount(),
                flowResult.phasesCount(),
                AssignmentRunStatus.SIMULATED,
                valSummary,
                assignmentDtos,
                graphVis,
                traceSummaries
        );
    }

    @Transactional
    public CommitResponse commitAllocation(UUID runId, CommitRequest request, String actorEmail, String ipAddress) {
        AssignmentRun run = assignmentRunRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment run not found with id: " + runId));

        if (run.getStatus() == AssignmentRunStatus.COMMITTED) {
            throw new BadRequestException("Assignment run is already committed");
        }

        User actor = userRepository.findByEmailIgnoreCase(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + actorEmail));

        Conference conference = run.getConference();
        List<Manuscript> manuscripts = manuscriptRepository.findByConference(conference);
        List<Reviewer> reviewers = reviewerRepository.findByConferenceAndActiveTrue(conference);
        List<ConflictOfInterest> conflicts = conflictRepository.findByConference(conference);

        // Re-execute canonical solver to ensure transactional purity
        List<ManuscriptNode> mNodes = manuscripts.stream().map(m -> new ManuscriptNode(
                m.getId().toString(), m.getTitle(), m.getTrack() != null ? m.getTrack().getName() : "General",
                m.getRequiredReviews(), m.getTopics(), m.getKeywords(), Set.of(m.getAuthor().getId().toString()), m.getAuthorAffiliations()
        )).toList();

        List<ReviewerNode> rNodes = reviewers.stream().map(r -> new ReviewerNode(
                r.getId().toString(), r.getUser().getFullName(), r.getUser().getEmail(), r.getAffiliation(),
                r.getMaxCapacity(), r.isActive(), r.isAvailable(), r.getTopics(), r.getKeywords()
        )).toList();

        List<ConflictDeclaration> cNodes = conflicts.stream().map(c -> new ConflictDeclaration(
                c.getManuscript().getId().toString(), c.getReviewer().getId().toString(), c.getConflictType().name(), c.getReason()
        )).toList();

        BipartiteGraphBuilder.BuildResult buildResult = BipartiteGraphBuilder.buildNetwork(
                mNodes, rNodes, cNodes, conference.getRequiredReviewsPerPaper()
        );

        MaxFlowAlgorithm algo = switch (run.getAlgorithm()) {
            case FORD_FULKERSON -> new FordFulkersonAlgorithm();
            case EDMONDS_KARP -> new EdmondsKarpAlgorithm();
            case DINIC -> new DinicAlgorithm();
        };

        FlowNetwork executionClone = buildResult.network().deepClone();
        algo.solve(executionClone);
        List<AssignmentExtractor.AssignedPair> assignedPairs = AssignmentExtractor.extractAssignments(executionClone);

        // Delete existing committed assignments for this run if any
        assignmentRepository.deleteByAssignmentRun(run);

        Map<UUID, Manuscript> manuscriptMap = new HashMap<>();
        manuscripts.forEach(m -> manuscriptMap.put(m.getId(), m));

        Map<UUID, Reviewer> reviewerMap = new HashMap<>();
        reviewers.forEach(r -> reviewerMap.put(r.getId(), r));

        List<Assignment> committedList = new ArrayList<>();
        Set<Manuscript> updatedManuscripts = new HashSet<>();

        for (AssignmentExtractor.AssignedPair pair : assignedPairs) {
            UUID mId = UUID.fromString(pair.manuscriptId());
            UUID rId = UUID.fromString(pair.reviewerId());

            Manuscript m = manuscriptMap.get(mId);
            Reviewer r = reviewerMap.get(rId);

            if (m != null && r != null) {
                Assignment assignment = new Assignment(run, conference, m, r, pair.flow());
                committedList.add(assignment);

                m.setStatus(ManuscriptStatus.UNDER_REVIEW);
                updatedManuscripts.add(m);
            }
        }

        assignmentRepository.saveAll(committedList);
        manuscriptRepository.saveAll(updatedManuscripts);

        run.setStatus(AssignmentRunStatus.COMMITTED);
        run.setCommittedBy(actor);
        run.setCommittedAt(Instant.now());
        assignmentRunRepository.save(run);

        auditService.log(actorEmail, AuditAction.ASSIGNMENT_COMMITTED, "ASSIGNMENT_RUN", run.getId().toString(),
                String.format("Committed %d assignments for conference %s", committedList.size(), conference.getCode()),
                ipAddress);

        return new CommitResponse(
                run.getId(),
                AssignmentRunStatus.COMMITTED,
                committedList.size(),
                run.getCommittedAt(),
                actor.getEmail()
        );
    }

    @Transactional
    public OverrideResponse overrideAssignment(OverrideRequest request, String actorEmail, String ipAddress) {
        Conference conference = conferenceRepository.findById(request.conferenceId())
                .orElseThrow(() -> new ResourceNotFoundException("Conference not found"));

        Manuscript manuscript = manuscriptRepository.findById(request.manuscriptId())
                .orElseThrow(() -> new ResourceNotFoundException("Manuscript not found"));

        Reviewer reviewer = reviewerRepository.findById(request.reviewerId())
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));

        // Conflict check
        if (conflictRepository.existsByManuscriptIdAndReviewerId(manuscript.getId(), reviewer.getId())) {
            throw new BadRequestException("Cannot override: Declared conflict of interest exists between manuscript and reviewer");
        }

        // Author check
        if (manuscript.getAuthor().getId().equals(reviewer.getUser().getId())) {
            throw new BadRequestException("Cannot override: Reviewer is an author of this manuscript");
        }

        // Find or create active assignment run
        AssignmentRun run = assignmentRunRepository.findFirstByConferenceAndStatusOrderByCommittedAtDesc(conference, AssignmentRunStatus.COMMITTED)
                .orElseGet(() -> {
                    AssignmentRun newRun = new AssignmentRun(conference, AlgorithmType.DINIC, "OVERRIDE-MANUAL", 1, 1, 4, 3, 1, 1, 100.0, 0.0, 0, 0);
                    newRun.setStatus(AssignmentRunStatus.COMMITTED);
                    return assignmentRunRepository.save(newRun);
                });

        Assignment assignment = new Assignment(run, conference, manuscript, reviewer, 1L);
        assignment.setManualOverride(true);
        assignment.setOverrideReason(request.overrideReason() != null ? request.overrideReason() : "Manual conference admin override");

        Assignment saved = assignmentRepository.save(assignment);
        run.setStatus(AssignmentRunStatus.OVERRIDDEN);
        assignmentRunRepository.save(run);

        auditService.log(actorEmail, AuditAction.ASSIGNMENT_OVERRIDDEN, "ASSIGNMENT", saved.getId().toString(),
                String.format("Manual override: Assigned '%s' to '%s'. Reason: %s", manuscript.getTitle(), reviewer.getUser().getFullName(), request.overrideReason()),
                ipAddress);

        return new OverrideResponse(saved.getId(), manuscript.getId(), reviewer.getId(), true, "Manual assignment override successfully applied");
    }

    @Transactional(readOnly = true)
    public AssignmentExplanationDto explainAssignment(UUID manuscriptId, UUID reviewerId, UUID runId) {
        Manuscript manuscript = manuscriptRepository.findById(manuscriptId)
                .orElseThrow(() -> new ResourceNotFoundException("Manuscript not found with id: " + manuscriptId));

        Reviewer reviewer = reviewerRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found with id: " + reviewerId));

        AssignmentRun run = runId != null ? assignmentRunRepository.findById(runId).orElse(null) : null;
        String algoName = run != null ? run.getAlgorithm().name() : "Dinic";
        String runIdStr = run != null ? run.getId().toString() : "SIMULATION";
        String fingerprint = run != null ? run.getGraphFingerprint() : "LIVE";

        ManuscriptNode mNode = new ManuscriptNode(
                manuscript.getId().toString(),
                manuscript.getTitle(),
                manuscript.getTrack() != null ? manuscript.getTrack().getName() : "General",
                manuscript.getRequiredReviews(),
                manuscript.getTopics(),
                manuscript.getKeywords(),
                Set.of(manuscript.getAuthor().getId().toString()),
                manuscript.getAuthorAffiliations()
        );

        ReviewerNode rNode = new ReviewerNode(
                reviewer.getId().toString(),
                reviewer.getUser().getFullName(),
                reviewer.getUser().getEmail(),
                reviewer.getAffiliation(),
                reviewer.getMaxCapacity(),
                reviewer.isActive(),
                reviewer.isAvailable(),
                reviewer.getTopics(),
                reviewer.getKeywords()
        );

        List<Assignment> currentAssignments = assignmentRepository.findByReviewerId(reviewer.getId());
        AssignmentExplanation explanation = AssignmentExplanation.build(
                mNode,
                rNode,
                currentAssignments.size(),
                algoName,
                runIdStr,
                fingerprint
        );

        return new AssignmentExplanationDto(
                manuscript.getId(),
                explanation.manuscriptTitle(),
                explanation.manuscriptTrack(),
                reviewer.getId(),
                explanation.reviewerName(),
                explanation.reviewerAffiliation(),
                explanation.topicOverlapCount(),
                explanation.matchingTopics(),
                explanation.keywordOverlapCount(),
                explanation.matchingKeywords(),
                explanation.compatibilityScore(),
                explanation.reviewerWorkloadAssigned(),
                explanation.reviewerMaxCapacity(),
                explanation.conflictFree(),
                explanation.conflictVerificationDetails(),
                explanation.algorithmName(),
                explanation.algorithmRunId(),
                explanation.flow(),
                explanation.graphFingerprint(),
                explanation.explanationSummary()
        );
    }

    private GraphVisualizationDto buildGraphVisualization(
            FlowNetwork solvedNetwork,
            BipartiteGraphBuilder.BuildResult buildResult,
            Map<String, Manuscript> mEntityMap,
            Map<String, Reviewer> rEntityMap
    ) {
        List<GraphNodeDto> nodes = new ArrayList<>();
        List<GraphEdgeDto> edges = new ArrayList<>();

        // Add Source node
        nodes.add(new GraphNodeDto("0", "SOURCE", "SOURCE", (int) solvedNetwork.getTotalSourceCapacity(), 0, Map.of("role", "source")));

        // Add Manuscript nodes
        for (Map.Entry<String, Integer> entry : buildResult.manuscriptVertexMap().entrySet()) {
            Manuscript m = mEntityMap.get(entry.getKey());
            String title = m != null ? m.getTitle() : "Manuscript " + entry.getKey();
            nodes.add(new GraphNodeDto(
                    String.valueOf(entry.getValue()),
                    title.length() > 25 ? title.substring(0, 22) + "..." : title,
                    "MANUSCRIPT",
                    m != null ? m.getRequiredReviews() : 2,
                    0,
                    Map.of("id", entry.getKey(), "title", title)
            ));
        }

        // Add Reviewer nodes
        for (Map.Entry<String, Integer> entry : buildResult.reviewerVertexMap().entrySet()) {
            Reviewer r = rEntityMap.get(entry.getKey());
            String name = r != null ? r.getUser().getFullName() : "Reviewer " + entry.getKey();
            nodes.add(new GraphNodeDto(
                    String.valueOf(entry.getValue()),
                    name.length() > 22 ? name.substring(0, 19) + "..." : name,
                    "REVIEWER",
                    r != null ? r.getMaxCapacity() : 4,
                    0,
                    Map.of("id", entry.getKey(), "name", name)
            ));
        }

        // Add Sink node
        nodes.add(new GraphNodeDto(String.valueOf(solvedNetwork.getSink()), "SINK", "SINK", (int) solvedNetwork.getTotalSinkCapacity(), 0, Map.of("role", "sink")));

        // Add Edges with flow & saturation state
        for (FlowEdge edge : solvedNetwork.getOriginalEdges()) {
            boolean saturated = edge.getFlow() > 0 && edge.getFlow() == edge.getCapacity();
            edges.add(new GraphEdgeDto(
                    String.valueOf(edge.getFrom()),
                    String.valueOf(edge.getTo()),
                    edge.getCapacity(),
                    edge.getFlow(),
                    saturated,
                    edge.getEdgeType().name(),
                    edge.getManuscriptId(),
                    edge.getReviewerId()
            ));
        }

        return new GraphVisualizationDto(nodes, edges);
    }
}
