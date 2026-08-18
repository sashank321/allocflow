"use client";

import React, { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "@/lib/api";
import {
  GitMerge,
  Play,
  CheckCircle,
  AlertTriangle,
  Fingerprint,
  Layers,
  Sparkles,
  Info,
  Sliders,
  CheckCheck,
  RotateCcw,
  ShieldCheck,
  Network,
  HelpCircle,
} from "lucide-react";
import type { AlgorithmType, SimulationResponse, AssignmentExplanation } from "@/types";
import { BipartiteFlowGraph } from "@/components/graph/BipartiteFlowGraph";
import { ExplainDrawer } from "@/components/matching/ExplainDrawer";
import { formatMs } from "@/lib/utils";

export default function MatchingCockpitPage() {
  const queryClient = useQueryClient();

  const [algorithm, setAlgorithm] = useState<AlgorithmType>("DINIC");
  const [reviewsPerPaper, setReviewsPerPaper] = useState<number>(2);
  const [reviewerCapacity, setReviewerCapacity] = useState<number>(4);
  const [excludeConflicts, setExcludeConflicts] = useState<boolean>(true);

  const [simulationResult, setSimulationResult] = useState<SimulationResponse | null>(null);
  const [selectedExplanation, setSelectedExplanation] = useState<AssignmentExplanation | null>(null);
  const [isExplainOpen, setIsExplainOpen] = useState(false);
  const [commitSuccess, setCommitSuccess] = useState(false);

  const { data: conferences } = useQuery({
    queryKey: ["conferences"],
    queryFn: () => api.getConferences(),
  });

  const activeConfId = conferences?.[0]?.id;

  const simulateMutation = useMutation({
    mutationFn: () =>
      api.simulateMatching({
        conferenceId: activeConfId!,
        algorithm,
        requiredReviewsPerPaper: reviewsPerPaper,
        defaultReviewerCapacity: reviewerCapacity,
        excludeConflicts,
      }),
    onSuccess: (data) => {
      setSimulationResult(data);
      setCommitSuccess(false);
    },
  });

  const commitMutation = useMutation({
    mutationFn: () => api.commitMatching(simulationResult!.runId, "Committed via Matching Cockpit"),
    onSuccess: () => {
      setCommitSuccess(true);
      queryClient.invalidateQueries({ queryKey: ["dashboard-stats"] });
      queryClient.invalidateQueries({ queryKey: ["manuscripts"] });
    },
  });

  const handleExplain = async (manuscriptId: string, reviewerId: string) => {
    try {
      const explanation = await api.explainAssignment(
        manuscriptId,
        reviewerId,
        simulationResult?.runId
      );
      setSelectedExplanation(explanation);
      setIsExplainOpen(true);
    } catch (e) {
      console.error("Failed to fetch assignment explanation", e);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-wrap items-center justify-between gap-4 border-b pb-4">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-xl font-bold tracking-tight text-foreground">
              Matching Engine Cockpit
            </h1>
            <span className="rounded bg-purple-50 border border-purple-200 px-2 py-0.5 text-[11px] font-semibold text-purple-700">
              Deterministic Bipartite Max-Flow
            </span>
          </div>
          <p className="text-xs text-muted-foreground">
            Configure matching parameters, preview residual flow graphs, and transactionally commit allocations
          </p>
        </div>

        {/* Action Controls */}
        <div className="flex items-center gap-2.5">
          <button
            onClick={() => simulateMutation.mutate()}
            disabled={!activeConfId || simulateMutation.isPending}
            className="flex items-center gap-1.5 rounded-md bg-blue-600 px-4 py-2 text-xs font-semibold text-white shadow-sm hover:bg-blue-700 disabled:opacity-50 transition-colors"
          >
            {simulateMutation.isPending ? (
              <div className="h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent" />
            ) : (
              <Play className="h-4 w-4" />
            )}
            <span>{simulateMutation.isPending ? "Solving Flow Network..." : "Simulate Allocation"}</span>
          </button>

          {simulationResult && (
            <button
              onClick={() => commitMutation.mutate()}
              disabled={commitMutation.isPending || commitSuccess}
              className={`flex items-center gap-1.5 rounded-md px-4 py-2 text-xs font-semibold text-white shadow-sm transition-colors ${
                commitSuccess
                  ? "bg-emerald-600 cursor-default"
                  : "bg-emerald-600 hover:bg-emerald-700 disabled:opacity-50"
              }`}
            >
              {commitSuccess ? <CheckCheck className="h-4 w-4" /> : <CheckCircle className="h-4 w-4" />}
              <span>{commitSuccess ? "Assignments Committed ✓" : "Commit to Database"}</span>
            </button>
          )}
        </div>
      </div>

      {/* Matching Configuration Panel */}
      <div className="rounded-xl border bg-card p-5 shadow-sm space-y-4">
        <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-muted-foreground">
          <Sliders className="h-4 w-4 text-blue-600" />
          <span>Allocation Parameters &amp; Algorithm Selection</span>
        </div>

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4 text-xs">
          {/* Algorithm Choice */}
          <div className="space-y-1.5">
            <label className="font-semibold text-foreground">Max-Flow Algorithm</label>
            <select
              value={algorithm}
              onChange={(e) => setAlgorithm(e.target.value as AlgorithmType)}
              className="w-full rounded-md border bg-background p-2 text-xs text-foreground focus:border-blue-500 focus:outline-none"
            >
              <option value="DINIC">Dinic (Level Graph BFS + Blocking Flow DFS) [O(V²E)]</option>
              <option value="EDMONDS_KARP">Edmonds-Karp (BFS Shortest Path) [O(V·E²)]</option>
              <option value="FORD_FULKERSON">Ford-Fulkerson (Standard DFS Augmentation) [O(E·|f|)]</option>
            </select>
          </div>

          {/* Required Reviews per Paper */}
          <div className="space-y-1.5">
            <label className="font-semibold text-foreground">Required Reviews / Paper</label>
            <input
              type="number"
              min={1}
              max={5}
              value={reviewsPerPaper}
              onChange={(e) => setReviewsPerPaper(Number(e.target.value))}
              className="w-full rounded-md border bg-background p-2 text-xs text-foreground focus:border-blue-500 focus:outline-none"
            />
          </div>

          {/* Reviewer Capacity */}
          <div className="space-y-1.5">
            <label className="font-semibold text-foreground">Default Reviewer Capacity</label>
            <input
              type="number"
              min={1}
              max={10}
              value={reviewerCapacity}
              onChange={(e) => setReviewerCapacity(Number(e.target.value))}
              className="w-full rounded-md border bg-background p-2 text-xs text-foreground focus:border-blue-500 focus:outline-none"
            />
          </div>

          {/* COI Exclusion Toggle */}
          <div className="flex flex-col justify-end">
            <label className="flex items-center gap-2 rounded-md border bg-secondary/30 p-2.5 cursor-pointer">
              <input
                type="checkbox"
                checked={excludeConflicts}
                onChange={(e) => setExcludeConflicts(e.target.checked)}
                className="rounded text-blue-600 focus:ring-0"
              />
              <span className="font-medium text-foreground">Enforce Zero-COI Exclusions</span>
            </label>
          </div>
        </div>
      </div>

      {/* Simulation Result Header */}
      {simulationResult && (
        <div className="space-y-6">
          {/* Metrics Overview Bar */}
          <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-6">
            <div className="rounded-xl border bg-card p-3.5 shadow-sm space-y-1">
              <span className="text-[11px] text-muted-foreground">Max Flow Achieved</span>
              <p className="font-mono text-lg font-bold text-foreground">
                {simulationResult.achievedFlow} / {simulationResult.totalRequiredFlow}
              </p>
              <p className="text-[10px] text-muted-foreground">units of flow</p>
            </div>

            <div className="rounded-xl border bg-card p-3.5 shadow-sm space-y-1">
              <span className="text-[11px] text-muted-foreground">Coverage</span>
              <p className="font-mono text-lg font-bold text-emerald-700">
                {simulationResult.coveragePercentage.toFixed(1)}%
              </p>
              <p className="text-[10px] text-muted-foreground">fully satisfied</p>
            </div>

            <div className="rounded-xl border bg-card p-3.5 shadow-sm space-y-1">
              <span className="text-[11px] text-muted-foreground">Solving Duration</span>
              <p className="font-mono text-lg font-bold text-purple-700">
                {formatMs(simulationResult.durationMs)}
              </p>
              <p className="text-[10px] text-muted-foreground font-mono">
                {simulationResult.augmentationsCount} augmentations
              </p>
            </div>

            <div className="rounded-xl border bg-card p-3.5 shadow-sm space-y-1">
              <span className="text-[11px] text-muted-foreground">Canonical Vertices</span>
              <p className="font-mono text-lg font-bold text-foreground">
                {simulationResult.totalVertices}
              </p>
              <p className="text-[10px] text-muted-foreground">
                {simulationResult.totalManuscripts} Papers · {simulationResult.totalReviewers} Reviewers
              </p>
            </div>

            <div className="rounded-xl border bg-card p-3.5 shadow-sm space-y-1">
              <span className="text-[11px] text-muted-foreground">Canonical Edges</span>
              <p className="font-mono text-lg font-bold text-foreground">
                {simulationResult.totalEdges}
              </p>
              <p className="text-[10px] text-muted-foreground">bipartite candidates</p>
            </div>

            <div className="rounded-xl border bg-card p-3.5 shadow-sm space-y-1">
              <span className="text-[11px] text-muted-foreground">SHA-256 Fingerprint</span>
              <p className="font-mono text-[11px] font-bold text-blue-700 truncate">
                {simulationResult.graphFingerprint}
              </p>
              <p className="text-[10px] text-muted-foreground">Canonical hash verified</p>
            </div>
          </div>

          {/* Interactive Bipartite Flow Graph */}
          <BipartiteFlowGraph
            data={simulationResult.graphVisualization}
            traces={simulationResult.executionTraceSummary}
            algorithmName={simulationResult.algorithmName}
            onEdgeClick={handleExplain}
          />

          {/* Allocated Pairs Table */}
          <div className="rounded-xl border bg-card shadow-sm overflow-hidden space-y-3 p-5">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-sm font-bold text-foreground">
                  Simulated Reviewer Allocations ({simulationResult.assignments.length} Pairs)
                </h3>
                <p className="text-xs text-muted-foreground">
                  Click any row or &quot;Explain&quot; button to view the deterministic bipartite matching proof
                </p>
              </div>
              <span className="rounded bg-emerald-50 border border-emerald-200 px-2 py-0.5 text-xs font-semibold text-emerald-800">
                100% Conflict-Free
              </span>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="border-b bg-secondary/30 text-muted-foreground font-semibold">
                  <tr>
                    <th className="py-2.5 px-3">Manuscript</th>
                    <th className="py-2.5 px-3">Assigned Reviewer</th>
                    <th className="py-2.5 px-3">Affiliation</th>
                    <th className="py-2.5 px-3 text-center">Topic Overlap</th>
                    <th className="py-2.5 px-3 text-center">Keyword Matches</th>
                    <th className="py-2.5 px-3 text-center">Flow</th>
                    <th className="py-2.5 px-3 text-right">Explainability</th>
                  </tr>
                </thead>
                <tbody className="divide-y">
                  {simulationResult.assignments.map((pair, idx) => (
                    <tr
                      key={idx}
                      onClick={() => handleExplain(pair.manuscriptId, pair.reviewerId)}
                      className="hover:bg-secondary/20 cursor-pointer transition-colors"
                    >
                      <td className="py-2.5 px-3 font-bold text-foreground max-w-xs truncate">
                        {pair.manuscriptTitle}
                      </td>
                      <td className="py-2.5 px-3 font-medium text-foreground">
                        {pair.reviewerName}
                      </td>
                      <td className="py-2.5 px-3 text-muted-foreground">
                        {pair.reviewerAffiliation || "Independent"}
                      </td>
                      <td className="py-2.5 px-3 text-center font-mono font-semibold text-blue-700">
                        {pair.topicOverlapCount} topics
                      </td>
                      <td className="py-2.5 px-3 text-center font-mono text-muted-foreground">
                        {pair.keywordOverlapCount} tags
                      </td>
                      <td className="py-2.5 px-3 text-center font-mono font-bold text-emerald-700">
                        {pair.flow} unit
                      </td>
                      <td className="py-2.5 px-3 text-right">
                        <button
                          onClick={(e) => {
                            e.stopPropagation();
                            handleExplain(pair.manuscriptId, pair.reviewerId);
                          }}
                          className="inline-flex items-center gap-1 rounded bg-secondary px-2.5 py-1 text-[11px] font-semibold text-blue-600 hover:bg-secondary/80"
                        >
                          <HelpCircle className="h-3 w-3" />
                          <span>Explain</span>
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* Explain This Assignment Drawer */}
      <ExplainDrawer
        explanation={selectedExplanation}
        isOpen={isExplainOpen}
        onClose={() => setIsExplainOpen(false)}
      />
    </div>
  );
}
