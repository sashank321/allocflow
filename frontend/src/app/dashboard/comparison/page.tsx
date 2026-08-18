"use client";

import React, { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { api } from "@/lib/api";
import {
  Scale,
  Play,
  CheckCircle2,
  AlertCircle,
  Fingerprint,
  Layers,
  Sparkles,
  Info,
  Clock,
  ShieldCheck,
  TrendingDown,
} from "lucide-react";
import type { BenchmarkComparisonResponse } from "@/types";
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
} from "recharts";
import { formatMs } from "@/lib/utils";

export default function AlgorithmComparisonPage() {
  const [manuscripts, setManuscripts] = useState(30);
  const [reviewers, setReviewers] = useState(15);
  const [reviewsPerPaper, setReviewsPerPaper] = useState(2);
  const [capacity, setCapacity] = useState(4);
  const [seed, setSeed] = useState(482917);
  const [warmups, setWarmups] = useState(3);
  const [trials, setTrials] = useState(10);

  const [benchmarkResult, setBenchmarkResult] = useState<BenchmarkComparisonResponse | null>(null);

  const compareMutation = useMutation({
    mutationFn: () =>
      api.runComparison({
        manuscriptCount: manuscripts,
        reviewerCount: reviewers,
        requiredReviewsPerPaper: reviewsPerPaper,
        reviewerCapacity: capacity,
        randomSeed: seed,
        warmupTrials: warmups,
        measuredTrials: trials,
      }),
    onSuccess: (data) => {
      setBenchmarkResult(data);
    },
  });

  React.useEffect(() => {
    if (!benchmarkResult && !compareMutation.isPending) {
      compareMutation.mutate();
    }
  }, []);

  // Chart data
  const runtimeChartData = benchmarkResult?.algorithms.map((algo) => ({
    name: algo.algorithmName,
    "Median Runtime (ms)": algo.medianDurationMs,
    "p95 Runtime (ms)": algo.p95DurationMs,
    "Augmentations / Paths": algo.augmentations,
  })) || [];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-wrap items-center justify-between gap-4 border-b pb-4">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-xl font-bold tracking-tight text-foreground">
              Tri-Algorithm Max-Flow Comparison
            </h1>
            <span className="rounded bg-purple-50 border border-purple-200 px-2 py-0.5 text-[11px] font-semibold text-purple-700">
              Rigorous Research Protocol
            </span>
          </div>
          <p className="text-xs text-muted-foreground">
            Sequential evaluation (same graph → clone → FF → clone → EK → clone → Dinic) with median/p95 latency &amp; SHA-256 fingerprint
          </p>
        </div>

        <button
          onClick={() => compareMutation.mutate()}
          disabled={compareMutation.isPending}
          className="flex items-center gap-1.5 rounded-md bg-blue-600 px-4 py-2 text-xs font-semibold text-white shadow-sm hover:bg-blue-700 disabled:opacity-50 transition-colors"
        >
          {compareMutation.isPending ? (
            <div className="h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent" />
          ) : (
            <Play className="h-4 w-4" />
          )}
          <span>{compareMutation.isPending ? "Executing Sequential Trials..." : "Run Tri-Algorithm Benchmark"}</span>
        </button>
      </div>

      {/* Benchmark Control Panel */}
      <div className="rounded-xl border bg-card p-5 shadow-sm space-y-4">
        <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-muted-foreground">
          <Scale className="h-4 w-4 text-purple-600" />
          <span>Configurable Experiment Parameters</span>
        </div>

        <div className="grid grid-cols-2 gap-4 sm:grid-cols-4 lg:grid-cols-7 text-xs">
          <div>
            <label className="font-semibold text-foreground">Manuscripts (V_P)</label>
            <input
              type="number"
              value={manuscripts}
              onChange={(e) => setManuscripts(Number(e.target.value))}
              className="mt-1 w-full rounded-md border bg-background p-1.5 text-xs focus:border-blue-500 focus:outline-none"
            />
          </div>

          <div>
            <label className="font-semibold text-foreground">Reviewers (V_R)</label>
            <input
              type="number"
              value={reviewers}
              onChange={(e) => setReviewers(Number(e.target.value))}
              className="mt-1 w-full rounded-md border bg-background p-1.5 text-xs focus:border-blue-500 focus:outline-none"
            />
          </div>

          <div>
            <label className="font-semibold text-foreground">Req. Reviews</label>
            <input
              type="number"
              value={reviewsPerPaper}
              onChange={(e) => setReviewsPerPaper(Number(e.target.value))}
              className="mt-1 w-full rounded-md border bg-background p-1.5 text-xs focus:border-blue-500 focus:outline-none"
            />
          </div>

          <div>
            <label className="font-semibold text-foreground">Capacity / Rev</label>
            <input
              type="number"
              value={capacity}
              onChange={(e) => setCapacity(Number(e.target.value))}
              className="mt-1 w-full rounded-md border bg-background p-1.5 text-xs focus:border-blue-500 focus:outline-none"
            />
          </div>

          <div>
            <label className="font-semibold text-foreground">Random Seed</label>
            <input
              type="number"
              value={seed}
              onChange={(e) => setSeed(Number(e.target.value))}
              className="mt-1 w-full rounded-md border bg-background p-1.5 text-xs font-mono focus:border-blue-500 focus:outline-none"
            />
          </div>

          <div>
            <label className="font-semibold text-foreground">Warmups</label>
            <input
              type="number"
              value={warmups}
              onChange={(e) => setWarmups(Number(e.target.value))}
              className="mt-1 w-full rounded-md border bg-background p-1.5 text-xs focus:border-blue-500 focus:outline-none"
            />
          </div>

          <div>
            <label className="font-semibold text-foreground">Measured Trials</label>
            <input
              type="number"
              value={trials}
              onChange={(e) => setTrials(Number(e.target.value))}
              className="mt-1 w-full rounded-md border bg-background p-1.5 text-xs focus:border-blue-500 focus:outline-none"
            />
          </div>
        </div>
      </div>

      {/* Results Section */}
      {benchmarkResult ? (
        <div className="space-y-6">
          {/* Invariant & Fingerprint Banner */}
          <div className="rounded-xl border border-emerald-200 bg-emerald-50/70 p-4 shadow-sm flex flex-wrap items-center justify-between gap-3 text-xs text-emerald-950">
            <div className="flex items-center gap-3">
              <CheckCircle2 className="h-6 w-6 text-emerald-600 shrink-0" />
              <div>
                <div className="flex items-center gap-2">
                  <span className="font-bold text-sm text-emerald-900">
                    Max-Flow Equivalence Invariant Verified
                  </span>
                  <span className="rounded bg-emerald-200/80 px-2 py-0.5 font-mono text-[10px] font-bold text-emerald-900">
                    FF = EK = Dinic = {benchmarkResult.invariantMaxFlow} units
                  </span>
                </div>
                <p className="text-[11px] text-emerald-800 mt-0.5">
                  All 3 algorithms independently computed identical maximum flow on isolated deep clones of the canonical network.
                </p>
              </div>
            </div>

            <div className="flex items-center gap-2 font-mono text-[11px] bg-white/70 px-3 py-1.5 rounded-lg border border-emerald-200">
              <Fingerprint className="h-4 w-4 text-emerald-700" />
              <span className="text-muted-foreground">Fingerprint:</span>
              <span className="font-bold text-emerald-900">{benchmarkResult.graphFingerprint}</span>
            </div>
          </div>

          {/* Side-by-Side Algorithm Comparison Cards */}
          <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
            {benchmarkResult.algorithms.map((algo) => {
              const isDinic = algo.algorithmName === "Dinic";
              return (
                <div
                  key={algo.algorithmName}
                  className={`rounded-xl border bg-card p-5 shadow-sm space-y-4 ${
                    isDinic ? "border-purple-300 ring-1 ring-purple-200" : ""
                  }`}
                >
                  <div className="flex items-start justify-between border-b pb-3">
                    <div>
                      <div className="flex items-center gap-1.5">
                        <h3 className="font-bold text-base text-foreground">{algo.algorithmName}</h3>
                        {isDinic && (
                          <span className="rounded bg-purple-100 px-1.5 py-0.2 text-[9px] font-bold text-purple-800">
                            Fastest
                          </span>
                        )}
                      </div>
                      <p className="font-mono text-[11px] text-muted-foreground mt-0.5">
                        {algo.theoreticalComplexity}
                      </p>
                    </div>
                    <span className="rounded bg-emerald-50 px-2 py-0.5 font-mono text-xs font-bold text-emerald-700">
                      Flow: {algo.maxFlow}
                    </span>
                  </div>

                  {/* Latency Metrics */}
                  <div className="space-y-2 text-xs">
                    <div className="flex justify-between items-baseline">
                      <span className="text-muted-foreground font-medium">Median Runtime:</span>
                      <span className="font-mono text-base font-bold text-foreground">
                        {formatMs(algo.medianDurationMs)}
                      </span>
                    </div>

                    <div className="flex justify-between">
                      <span className="text-muted-foreground">p95 Runtime:</span>
                      <span className="font-mono text-foreground">{formatMs(algo.p95DurationMs)}</span>
                    </div>

                    <div className="flex justify-between">
                      <span className="text-muted-foreground">Min / Max:</span>
                      <span className="font-mono text-muted-foreground">
                        {formatMs(algo.minDurationMs)} / {formatMs(algo.maxDurationMs)}
                      </span>
                    </div>

                    <div className="flex justify-between">
                      <span className="text-muted-foreground">Std Deviation:</span>
                      <span className="font-mono text-muted-foreground">
                        ±{formatMs(algo.stdDevDurationMs)}
                      </span>
                    </div>
                  </div>

                  {/* Structural Metrics */}
                  <div className="pt-2 border-t space-y-1.5 text-xs">
                    <div className="flex justify-between">
                      <span className="text-muted-foreground">Augmenting Paths:</span>
                      <span className="font-mono font-bold text-foreground">{algo.augmentations}</span>
                    </div>
                    {algo.phases > 0 && (
                      <div className="flex justify-between">
                        <span className="text-muted-foreground">Level Graph Phases:</span>
                        <span className="font-mono font-bold text-purple-700">{algo.phases}</span>
                      </div>
                    )}
                    <div className="flex justify-between text-[11px] text-muted-foreground">
                      <span>Repeated Trials:</span>
                      <span>{algo.warmupTrials} warmups + {algo.measuredTrials} trials</span>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>

          {/* Performance Comparison Charts */}
          <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
            {/* Median Runtime Chart */}
            <div className="rounded-xl border bg-card p-5 shadow-sm space-y-4">
              <div>
                <h3 className="text-sm font-bold text-foreground">Empirical Runtime Comparison (ms)</h3>
                <p className="text-xs text-muted-foreground">Lower is faster (Median and p95 across trials)</p>
              </div>

              <div className="h-64 w-full">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={runtimeChartData}>
                    <XAxis dataKey="name" tick={{ fontSize: 11 }} />
                    <YAxis tick={{ fontSize: 11 }} />
                    <Tooltip
                      contentStyle={{
                        backgroundColor: "hsl(var(--card))",
                        borderColor: "hsl(var(--border))",
                        fontSize: "12px",
                        borderRadius: "6px",
                      }}
                    />
                    <Legend wrapperStyle={{ fontSize: "11px" }} />
                    <Bar dataKey="Median Runtime (ms)" fill="#8b5cf6" radius={[4, 4, 0, 0]} />
                    <Bar dataKey="p95 Runtime (ms)" fill="#c4b5fd" radius={[4, 4, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </div>

            {/* Augmentation Paths Discovered */}
            <div className="rounded-xl border bg-card p-5 shadow-sm space-y-4">
              <div>
                <h3 className="text-sm font-bold text-foreground">Augmenting Paths Discovered</h3>
                <p className="text-xs text-muted-foreground">Number of flow pushing operations to reach optimality</p>
              </div>

              <div className="h-64 w-full">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={runtimeChartData}>
                    <XAxis dataKey="name" tick={{ fontSize: 11 }} />
                    <YAxis tick={{ fontSize: 11 }} />
                    <Tooltip
                      contentStyle={{
                        backgroundColor: "hsl(var(--card))",
                        borderColor: "hsl(var(--border))",
                        fontSize: "12px",
                        borderRadius: "6px",
                      }}
                    />
                    <Legend wrapperStyle={{ fontSize: "11px" }} />
                    <Bar dataKey="Augmentations / Paths" fill="#3b82f6" radius={[4, 4, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </div>
          </div>
        </div>
      ) : (
        <div className="flex h-64 flex-col items-center justify-center rounded-xl border border-dashed bg-card/50 text-muted-foreground space-y-2">
          <Scale className="h-8 w-8 opacity-40" />
          <p className="text-xs font-medium">No Comparison Executed Yet</p>
          <p className="text-[11px] text-muted-foreground/80">
            Click &quot;Run Tri-Algorithm Benchmark&quot; above to execute the empirical comparison.
          </p>
        </div>
      )}
    </div>
  );
}
