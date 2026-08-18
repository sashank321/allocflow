"use client";

import React from "react";
import {
  X,
  CheckCircle2,
  AlertCircle,
  FileText,
  User,
  Tag,
  Hash,
  Activity,
  Layers,
  Fingerprint,
  Sparkles,
} from "lucide-react";
import type { AssignmentExplanation } from "@/types";

interface ExplainDrawerProps {
  explanation: AssignmentExplanation | null;
  isOpen: boolean;
  onClose: () => void;
}

export function ExplainDrawer({ explanation, isOpen, onClose }: ExplainDrawerProps) {
  if (!isOpen || !explanation) return null;

  return (
    <div className="fixed inset-0 z-50 flex justify-end bg-black/70 backdrop-blur-md transition-opacity">
      <div className="flex h-full w-full max-w-lg flex-col border-l border-white/10 bg-black/90 p-6 shadow-2xl backdrop-blur-2xl animate-in slide-in-from-right duration-300 text-white">
        {/* Header */}
        <div className="flex items-center justify-between border-b border-white/10 pb-4">
          <div className="flex items-center gap-3">
            <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-gradient-to-br from-blue-500/20 to-purple-500/20 border border-white/20 text-cyan-300 shadow-md">
              <Sparkles className="h-5 w-5" />
            </div>
            <div>
              <h2 className="text-base font-bold tracking-tight text-white">Assignment Explanation</h2>
              <p className="text-xs text-muted-foreground">Deterministic Bipartite Matching Proof</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="btn-3d rounded-lg p-1.5 text-muted-foreground hover:text-white transition-colors"
          >
            <X className="h-4 w-4" />
          </button>
        </div>

        {/* Content Body */}
        <div className="flex-1 overflow-y-auto py-5 space-y-4">
          {/* Plain English Summary Pill */}
          <div className="glass-panel p-4 rounded-xl border border-emerald-500/30 bg-emerald-950/30 text-xs">
            <div className="flex items-center gap-2 font-bold text-emerald-300">
              <CheckCircle2 className="h-4 w-4 text-emerald-400 shrink-0" />
              <span>Valid Match Verified</span>
            </div>
            <p className="mt-1.5 text-[11px] leading-relaxed text-emerald-200/90">
              {explanation.explanationSummary}
            </p>
          </div>

          {/* Manuscript & Reviewer Cards */}
          <div className="grid grid-cols-2 gap-3">
            {/* Manuscript Card */}
            <div className="glass-panel p-3.5 space-y-1.5 rounded-xl border border-white/10">
              <div className="flex items-center gap-1.5 text-[11px] font-semibold text-cyan-300">
                <FileText className="h-3.5 w-3.5" />
                <span>Manuscript</span>
              </div>
              <p className="font-bold text-xs text-white line-clamp-2">{explanation.manuscriptTitle}</p>
              {explanation.manuscriptTrack && (
                <span className="inline-block rounded-md bg-cyan-500/10 border border-cyan-500/30 px-2 py-0.5 text-[9px] font-medium text-cyan-300">
                  {explanation.manuscriptTrack}
                </span>
              )}
            </div>

            {/* Reviewer Card */}
            <div className="glass-panel p-3.5 space-y-1.5 rounded-xl border border-white/10">
              <div className="flex items-center gap-1.5 text-[11px] font-semibold text-purple-300">
                <User className="h-3.5 w-3.5" />
                <span>Assigned Reviewer</span>
              </div>
              <p className="font-bold text-xs text-white">{explanation.reviewerName}</p>
              <p className="text-[11px] text-muted-foreground">{explanation.reviewerAffiliation || "Independent"}</p>
            </div>
          </div>

          {/* Match Criteria & Overlaps */}
          <div className="glass-panel p-4 space-y-3 rounded-xl border border-white/10">
            <h3 className="text-xs font-bold uppercase tracking-wider text-muted-foreground">
              Explainable Compatibility Criteria
            </h3>

            {/* Topic Overlap */}
            <div className="space-y-1.5">
              <div className="flex items-center justify-between text-xs">
                <span className="flex items-center gap-1.5 text-muted-foreground">
                  <Tag className="h-3.5 w-3.5 text-cyan-400" /> Topic Overlap:
                </span>
                <span className="font-semibold text-white">
                  {explanation.topicOverlapCount} matches
                </span>
              </div>
              <div className="flex flex-wrap gap-1.5">
                {explanation.matchingTopics && explanation.matchingTopics.length > 0 ? (
                  explanation.matchingTopics.map((topic, i) => (
                    <span
                      key={i}
                      className="rounded-md bg-cyan-500/10 border border-cyan-500/30 px-2 py-0.5 text-[10px] font-medium text-cyan-300"
                    >
                      {topic}
                    </span>
                  ))
                ) : (
                  <span className="text-[10px] text-muted-foreground italic">General track alignment</span>
                )}
              </div>
            </div>

            {/* Keyword Matches */}
            <div className="space-y-1.5 pt-2.5 border-t border-white/10">
              <div className="flex items-center justify-between text-xs">
                <span className="flex items-center gap-1.5 text-muted-foreground">
                  <Hash className="h-3.5 w-3.5 text-purple-400" /> Keyword Matches:
                </span>
                <span className="font-semibold text-white">
                  {explanation.keywordOverlapCount} matches
                </span>
              </div>
              <div className="flex flex-wrap gap-1.5">
                {explanation.matchingKeywords && explanation.matchingKeywords.length > 0 ? (
                  explanation.matchingKeywords.map((kw, i) => (
                    <span
                      key={i}
                      className="rounded-md bg-purple-500/10 border border-purple-500/30 px-2 py-0.5 text-[10px] font-medium text-purple-300"
                    >
                      #{kw}
                    </span>
                  ))
                ) : (
                  <span className="text-[10px] text-muted-foreground italic">Domain broad match</span>
                )}
              </div>
            </div>

            {/* Compatibility Score */}
            <div className="flex items-center justify-between pt-2.5 border-t border-white/10 text-xs">
              <span className="text-muted-foreground">Compatibility Score:</span>
              <span className="font-mono font-bold text-emerald-400 text-sm">
                {(explanation.compatibilityScore * 100).toFixed(0)}%
              </span>
            </div>
          </div>

          {/* Reviewer Capacity & COI Verification */}
          <div className="glass-panel p-4 space-y-3 rounded-xl border border-white/10">
            <h3 className="text-xs font-bold uppercase tracking-wider text-muted-foreground">
              Constraint &amp; Conflict Verification
            </h3>

            <div className="space-y-2 text-xs">
              <div className="flex items-center justify-between">
                <span className="text-muted-foreground">Reviewer Capacity:</span>
                <span className="font-medium text-white">
                  {explanation.reviewerWorkloadAssigned} / {explanation.reviewerMaxCapacity} slots used
                </span>
              </div>
              <div className="h-2 w-full rounded-full bg-white/10 overflow-hidden">
                <div
                  className="h-full bg-gradient-to-r from-cyan-500 to-blue-500 rounded-full transition-all"
                  style={{
                    width: `${Math.min(
                      100,
                      (explanation.reviewerWorkloadAssigned / (explanation.reviewerMaxCapacity || 1)) * 100
                    )}%`,
                  }}
                />
              </div>

              <div className="pt-2 border-t border-white/10 flex items-start gap-2 text-[11px] text-muted-foreground">
                <CheckCircle2 className="h-4 w-4 text-emerald-400 shrink-0 mt-0.5" />
                <span>{explanation.conflictVerificationDetails}</span>
              </div>
            </div>
          </div>

          {/* Algorithmic Provenance */}
          <div className="glass-panel p-4 space-y-2.5 text-xs rounded-xl border border-white/10 bg-white/[0.02]">
            <h3 className="text-xs font-bold uppercase tracking-wider text-muted-foreground">
              Algorithmic Execution Provenance
            </h3>
            <div className="flex justify-between">
              <span className="text-muted-foreground">Algorithm:</span>
              <span className="font-semibold text-purple-300">{explanation.algorithmName}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-muted-foreground">Flow Pushed:</span>
              <span className="font-mono font-bold text-white">{explanation.flow} unit</span>
            </div>
            <div className="flex justify-between">
              <span className="text-muted-foreground">Run ID:</span>
              <span className="font-mono text-[10px] text-muted-foreground truncate max-w-[200px]">
                {explanation.algorithmRunId}
              </span>
            </div>
            <div className="pt-2 border-t border-white/10 flex items-center justify-between text-[10px] text-muted-foreground">
              <span className="flex items-center gap-1">
                <Fingerprint className="h-3.5 w-3.5 text-cyan-400" /> Graph Fingerprint:
              </span>
              <span className="font-mono truncate max-w-[160px] text-white/80">{explanation.graphFingerprint}</span>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="border-t border-white/10 pt-4">
          <button
            onClick={onClose}
            className="btn-3d w-full rounded-xl py-2.5 text-xs font-semibold text-white"
          >
            Close Explanation
          </button>
        </div>
      </div>
    </div>
  );
}
