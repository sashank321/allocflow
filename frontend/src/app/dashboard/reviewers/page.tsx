"use client";

import React, { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { api } from "@/lib/api";
import {
  Users,
  Search,
  CheckCircle2,
  XCircle,
  Building2,
  Mail,
  Tag,
  Hash,
  Activity,
  Plus,
} from "lucide-react";
import type { Reviewer } from "@/types";

export default function ReviewersPage() {
  const [searchTerm, setSearchTerm] = useState("");

  const { data: conferences } = useQuery({
    queryKey: ["conferences"],
    queryFn: () => api.getConferences(),
  });

  const activeConfId = conferences?.[0]?.id;

  const { data: reviewers, isLoading } = useQuery({
    queryKey: ["reviewers", activeConfId],
    queryFn: () => api.getReviewers(activeConfId),
    enabled: !!activeConfId,
  });

  const filteredReviewers = (reviewers || []).filter((r) => {
    return (
      r.userName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (r.affiliation && r.affiliation.toLowerCase().includes(searchTerm.toLowerCase())) ||
      r.topics.some((t) => t.toLowerCase().includes(searchTerm.toLowerCase()))
    );
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-wrap items-center justify-between gap-4 border-b pb-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-foreground">
            Reviewers &amp; Program Committee
          </h1>
          <p className="text-xs text-muted-foreground">
            Reviewer capacity headroom, topic expertise, and workload saturation
          </p>
        </div>

        <div className="flex items-center gap-2">
          <span className="rounded bg-purple-50 border border-purple-200 px-2.5 py-1 text-xs font-semibold text-purple-700">
            {reviewers?.length || 0} Registered Reviewers
          </span>
        </div>
      </div>

      {/* Search Input */}
      <div className="relative w-full max-w-sm">
        <Search className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
        <input
          type="text"
          placeholder="Search reviewers by name, affiliation, topic..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full rounded-md border bg-card py-2 pl-9 pr-4 text-xs text-foreground placeholder:text-muted-foreground focus:border-blue-500 focus:outline-none"
        />
      </div>

      {/* Reviewer Cards Grid */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {isLoading ? (
          <div className="col-span-full py-12 text-center text-xs text-muted-foreground">
            Loading reviewer roster...
          </div>
        ) : filteredReviewers.length > 0 ? (
          filteredReviewers.map((r) => {
            const utilizationPct = (r.currentWorkload / (r.maxCapacity || 1)) * 100;
            return (
              <div
                key={r.id}
                className="rounded-xl border bg-card p-4 shadow-sm hover:border-blue-300 transition-all space-y-3"
              >
                <div className="flex items-start justify-between">
                  <div>
                    <h3 className="font-bold text-sm text-foreground">{r.userName}</h3>
                    <p className="text-[11px] text-muted-foreground flex items-center gap-1 mt-0.5">
                      <Building2 className="h-3 w-3" />
                      {r.affiliation || "Independent Scholar"}
                    </p>
                  </div>
                  <div className="flex items-center gap-1">
                    {r.active && r.available ? (
                      <span className="flex items-center gap-1 rounded bg-emerald-50 px-1.5 py-0.5 text-[9px] font-semibold text-emerald-700">
                        <CheckCircle2 className="h-3 w-3" /> Available
                      </span>
                    ) : (
                      <span className="flex items-center gap-1 rounded bg-rose-50 px-1.5 py-0.5 text-[9px] font-semibold text-rose-700">
                        <XCircle className="h-3 w-3" /> Inactive
                      </span>
                    )}
                  </div>
                </div>

                {/* Capacity & Workload Bar */}
                <div className="space-y-1 text-xs">
                  <div className="flex justify-between text-[11px]">
                    <span className="text-muted-foreground">Workload Saturation:</span>
                    <span className="font-mono font-bold text-foreground">
                      {r.currentWorkload} / {r.maxCapacity} slots ({utilizationPct.toFixed(0)}%)
                    </span>
                  </div>
                  <div className="h-2 w-full rounded-full bg-secondary overflow-hidden">
                    <div
                      className={`h-full rounded-full transition-all ${
                        utilizationPct >= 100
                          ? "bg-rose-500"
                          : utilizationPct >= 50
                          ? "bg-amber-500"
                          : "bg-blue-600"
                      }`}
                      style={{ width: `${Math.min(100, utilizationPct)}%` }}
                    />
                  </div>
                </div>

                {/* Topic Expertise Badges */}
                <div className="space-y-1.5 pt-2 border-t text-xs">
                  <span className="text-[10px] font-semibold uppercase text-muted-foreground">
                    Topic Expertise:
                  </span>
                  <div className="flex flex-wrap gap-1">
                    {r.topics.map((t, idx) => (
                      <span
                        key={idx}
                        className="rounded bg-purple-50 border border-purple-200/60 px-1.5 py-0.5 text-[10px] font-medium text-purple-800"
                      >
                        {t}
                      </span>
                    ))}
                  </div>
                </div>

                {/* Keywords */}
                {r.keywords && r.keywords.length > 0 && (
                  <div className="flex flex-wrap gap-1 pt-1">
                    {r.keywords.map((k, idx) => (
                      <span
                        key={idx}
                        className="rounded bg-secondary px-1.5 py-0.5 text-[9px] text-muted-foreground"
                      >
                        #{k}
                      </span>
                    ))}
                  </div>
                )}
              </div>
            );
          })
        ) : (
          <div className="col-span-full py-12 text-center text-xs text-muted-foreground">
            No reviewers found matching your query.
          </div>
        )}
      </div>
    </div>
  );
}
