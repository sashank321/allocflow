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
    <div className="fixed inset-0 z-50 flex justify-end bg-black/40 backdrop-blur-sm transition-opacity">
      <div className="flex h-full w-full max-w-lg flex-col border-l bg-card p-6 shadow-2xl animate-in slide-in-from-right duration-200">
        {/* Header */}
        <div className="flex items-center justify-between border-b pb-4">
          <div className="flex items-center gap-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-md bg-blue-100 text-blue-700">
              <Layers className="h-4 w-4" />
            </div>
            <div>
              <h2 className="text-base font-bold text-foreground">Assignment Explanation</h2>
              <p className="text-xs text-muted-foreground">Deterministic Bipartite Matching Proof</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="rounded-md p-1 text-muted-foreground hover:bg-secondary hover:text-foreground"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* Content Body */}
        <div className="flex-1 overflow-y-auto py-5 space-y-5">
          {/* Plain English Summary Pill */}
          <div className="rounded-lg border border-emerald-200 bg-emerald-50/70 p-3.5 text-xs text-emerald-950">
            <div className="flex items-center gap-2 font-semibold text-emerald-800">
              <CheckCircle2 className="h-4 w-4 text-emerald-600 shrink-0" />
              <span>Valid Match Verified</span>
            </div>
            <p className="mt-1 text-[11px] leading-relaxed text-emerald-900">
              {explanation.explanationSummary}
            </p>
          </div>

          {/* Manuscript & Reviewer Cards */}
          <div className="grid grid-cols-2 gap-3">
            {/* Manuscript Card */}
            <div className="rounded-lg border bg-secondary/30 p-3 space-y-1">
              <div className="flex items-center gap-1.5 text-[11px] font-semibold text-muted-foreground">
                <FileText className="h-3.5 w-3.5 text-blue-600" />
                <span>Manuscript</span>
              </div>
              <p className="font-bold text-xs text-foreground line-clamp-2">{explanation.manuscriptTitle}</p>
              {explanation.manuscriptTrack && (
                <span className="inline-block rounded bg-blue-100/60 px-1.5 py-0.5 text-[10px] font-medium text-blue-800">
                  {explanation.manuscriptTrack}
                </span>
              )}
            </div>

            {/* Reviewer Card */}
            <div className="rounded-lg border bg-secondary/30 p-3 space-y-1">
              <div className="flex items-center gap-1.5 text-[11px] font-semibold text-muted-foreground">
                <User className="h-3.5 w-3.5 text-purple-600" />
                <span>Assigned Reviewer</span>
              </div>
              <p className="font-bold text-xs text-foreground">{explanation.reviewerName}</p>
              <p className="text-[11px] text-muted-foreground">{explanation.reviewerAffiliation || "Independent"}</p>
            </div>
          </div>

          {/* Match Criteria & Overlaps */}
          <div className="rounded-lg border p-4 space-y-3">
            <h3 className="text-xs font-bold uppercase tracking-wider text-muted-foreground">
              Explainable Compatibility Criteria
            </h3>

            {/* Topic Overlap */}
            <div className="space-y-1.5">
              <div className="flex items-center justify-between text-xs">
                <span className="flex items-center gap-1.5 text-muted-foreground">
                  <Tag className="h-3.5 w-3.5 text-blue-600" /> Topic Overlap:
                </span>
                <span className="font-semibold text-foreground">
                  {explanation.topicOverlapCount} matches
                </span>
              </div>
              <div className="flex flex-wrap gap-1">
                {explanation.matchingTopics && explanation.matchingTopics.length > 0 ? (
                  explanation.matchingTopics.map((topic, i) => (
                    <span
                      key={i}
                      className="rounded bg-blue-50 border border-blue-200 px-2 py-0.5 text-[10px] font-medium text-blue-700"
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
            <div className="space-y-1.5 pt-2 border-t">
              <div className="flex items-center justify-between text-xs">
                <span className="flex items-center gap-1.5 text-muted-foreground">
                  <Hash className="h-3.5 w-3.5 text-indigo-600" /> Keyword Matches:
                </span>
                <span className="font-semibold text-foreground">
                  {explanation.keywordOverlapCount} matches
                </span>
              </div>
              <div className="flex flex-wrap gap-1">
                {explanation.matchingKeywords && explanation.matchingKeywords.length > 0 ? (
                  explanation.matchingKeywords.map((kw, i) => (
                    <span
                      key={i}
                      className="rounded bg-indigo-50 border border-indigo-200 px-2 py-0.5 text-[10px] font-medium text-indigo-700"
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
            <div className="flex items-center justify-between pt-2 border-t text-xs">
              <span className="text-muted-foreground">Compatibility Score:</span>
              <span className="font-mono font-bold text-foreground">
                {(explanation.compatibilityScore * 100).toFixed(0)}%
              </span>
            </div>
          </div>

          {/* Reviewer Capacity & COI Verification */}
          <div className="rounded-lg border p-4 space-y-3">
            <h3 className="text-xs font-bold uppercase tracking-wider text-muted-foreground">
              Constraint &amp; Conflict Verification
            </h3>

            <div className="space-y-2 text-xs">
              <div className="flex items-center justify-between">
                <span className="text-muted-foreground">Reviewer Capacity:</span>
                <span className="font-medium text-foreground">
                  {explanation.reviewerWorkloadAssigned} / {explanation.reviewerMaxCapacity} slots used
                </span>
              </div>
              <div className="h-2 w-full rounded-full bg-secondary overflow-hidden">
                <div
                  className="h-full bg-blue-600 rounded-full transition-all"
                  style={{
                    width: `${Math.min(
                      100,
                      (explanation.reviewerWorkloadAssigned / (explanation.reviewerMaxCapacity || 1)) * 100
                    )}%`,
                  }}
                />
              </div>

              <div className="pt-2 border-t flex items-start gap-2 text-[11px] text-muted-foreground">
                <CheckCircle2 className="h-4 w-4 text-emerald-600 shrink-0 mt-0.5" />
                <span>{explanation.conflictVerificationDetails}</span>
              </div>
            </div>
          </div>

          {/* Algorithmic Provenance */}
          <div className="rounded-lg border bg-secondary/20 p-4 space-y-2 text-xs">
            <h3 className="text-xs font-bold uppercase tracking-wider text-muted-foreground">
              Algorithmic Execution Provenance
            </h3>
            <div className="flex justify-between">
              <span className="text-muted-foreground">Algorithm:</span>
              <span className="font-semibold text-purple-700">{explanation.algorithmName}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-muted-foreground">Flow Pushed:</span>
              <span className="font-mono font-bold text-foreground">{explanation.flow} unit</span>
            </div>
            <div className="flex justify-between">
              <span className="text-muted-foreground">Run ID:</span>
              <span className="font-mono text-[10px] text-muted-foreground truncate max-w-[200px]">
                {explanation.algorithmRunId}
              </span>
            </div>
            <div className="pt-2 border-t flex items-center justify-between text-[10px] text-muted-foreground">
              <span className="flex items-center gap-1">
                <Fingerprint className="h-3.5 w-3.5 text-blue-600" /> Graph Fingerprint:
              </span>
              <span className="font-mono truncate max-w-[160px]">{explanation.graphFingerprint}</span>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="border-t pt-4">
          <button
            onClick={onClose}
            className="w-full rounded-md bg-secondary py-2 text-xs font-semibold text-secondary-foreground hover:bg-secondary/80 transition-colors"
          >
            Close Explanation
          </button>
        </div>
      </div>
    </div>
  );
}
