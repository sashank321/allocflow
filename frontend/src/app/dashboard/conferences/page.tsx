"use client";

import React, { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "@/lib/api";
import {
  Building2,
  Calendar,
  Layers,
  Plus,
  Clock,
  CheckCircle2,
  X,
} from "lucide-react";
import type { Conference } from "@/types";

export default function ConferencesPage() {
  const queryClient = useQueryClient();
  const [isModalOpen, setIsModalOpen] = useState(false);

  // Form state
  const [code, setCode] = useState("");
  const [name, setName] = useState("");
  const [acronym, setAcronym] = useState("");
  const [description, setDescription] = useState("");
  const [requiredReviews, setRequiredReviews] = useState(2);
  const [defaultCapacity, setDefaultCapacity] = useState(4);

  const { data: conferences, isLoading } = useQuery({
    queryKey: ["conferences"],
    queryFn: () => api.getConferences(),
  });

  const createMutation = useMutation({
    mutationFn: (payload: any) => api.createConference(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["conferences"] });
      setIsModalOpen(false);
      setCode("");
      setName("");
      setDescription("");
    },
  });

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    if (!code.trim() || !name.trim()) return;

    createMutation.mutate({
      code: code.trim(),
      name: name.trim(),
      acronym: acronym.trim(),
      description: description.trim(),
      requiredReviewsPerPaper: requiredReviews,
      defaultReviewerCapacity: defaultCapacity,
    });
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-wrap items-center justify-between gap-4 border-b pb-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-foreground">
            Conferences &amp; Academic Cycles
          </h1>
          <p className="text-xs text-muted-foreground">
            Multi-conference SaaS configuration and review period windows
          </p>
        </div>

        <button
          onClick={() => setIsModalOpen(true)}
          className="flex items-center gap-1.5 rounded-md bg-blue-600 px-3.5 py-2 text-xs font-semibold text-white shadow-sm hover:bg-blue-700 transition-colors"
        >
          <Plus className="h-4 w-4" />
          <span>New Conference Cycle</span>
        </button>
      </div>

      {/* Conference Cards */}
      <div className="grid grid-cols-1 gap-5 md:grid-cols-2">
        {isLoading ? (
          <div className="col-span-full py-12 text-center text-xs text-muted-foreground">
            Loading conferences...
          </div>
        ) : conferences && conferences.length > 0 ? (
          conferences.map((c) => (
            <div
              key={c.id}
              className="rounded-xl border bg-card p-5 shadow-sm space-y-4 hover:border-blue-300 transition-all"
            >
              <div className="flex items-start justify-between">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-mono font-bold text-base text-blue-700">{c.code}</span>
                    <span className="rounded bg-emerald-100 px-1.5 py-0.2 text-[9px] font-bold text-emerald-800">
                      {c.status}
                    </span>
                  </div>
                  <h3 className="font-bold text-sm text-foreground mt-1">{c.name}</h3>
                </div>
              </div>

              <p className="text-xs text-muted-foreground leading-relaxed">
                {c.description || "No description provided."}
              </p>

              <div className="grid grid-cols-2 gap-3 pt-2 border-t text-xs">
                <div className="space-y-1">
                  <span className="text-[11px] text-muted-foreground">Required Reviews / Paper:</span>
                  <p className="font-mono font-bold text-foreground">{c.requiredReviewsPerPaper} reviews</p>
                </div>
                <div className="space-y-1">
                  <span className="text-[11px] text-muted-foreground">Default Reviewer Capacity:</span>
                  <p className="font-mono font-bold text-foreground">{c.defaultReviewerCapacity} papers</p>
                </div>
              </div>

              <div className="flex items-center justify-between pt-2 border-t text-[11px] text-muted-foreground">
                <span>{c.manuscriptCount} Manuscripts</span>
                <span>{c.reviewerCount} Reviewers</span>
                <span className="text-emerald-700 font-semibold">Ready for Matching</span>
              </div>
            </div>
          ))
        ) : (
          <div className="col-span-full py-12 text-center text-xs text-muted-foreground">
            No conferences configured.
          </div>
        )}
      </div>

      {/* Create Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="w-full max-w-md rounded-xl border bg-card p-6 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b pb-3">
              <h2 className="text-base font-bold text-foreground">Create Conference</h2>
              <button onClick={() => setIsModalOpen(false)} className="rounded-md p-1 text-muted-foreground hover:bg-secondary">
                <X className="h-4 w-4" />
              </button>
            </div>

            <form onSubmit={handleCreate} className="space-y-3 text-xs">
              <div>
                <label className="font-semibold text-foreground">Conference Code *</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. SIGMOD-2026"
                  value={code}
                  onChange={(e) => setCode(e.target.value)}
                  className="mt-1 w-full rounded-md border bg-background p-2 text-xs focus:border-blue-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="font-semibold text-foreground">Full Name *</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. ACM SIGMOD International Conference on Management of Data"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  className="mt-1 w-full rounded-md border bg-background p-2 text-xs focus:border-blue-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="font-semibold text-foreground">Description</label>
                <textarea
                  rows={2}
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  className="mt-1 w-full rounded-md border bg-background p-2 text-xs focus:border-blue-500 focus:outline-none"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-semibold text-foreground">Reviews Per Paper</label>
                  <input
                    type="number"
                    min={1}
                    max={5}
                    value={requiredReviews}
                    onChange={(e) => setRequiredReviews(Number(e.target.value))}
                    className="mt-1 w-full rounded-md border bg-background p-2 text-xs"
                  />
                </div>
                <div>
                  <label className="font-semibold text-foreground">Default Capacity</label>
                  <input
                    type="number"
                    min={1}
                    max={10}
                    value={defaultCapacity}
                    onChange={(e) => setDefaultCapacity(Number(e.target.value))}
                    className="mt-1 w-full rounded-md border bg-background p-2 text-xs"
                  />
                </div>
              </div>

              <div className="flex justify-end gap-2 pt-3 border-t">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="rounded-md border px-3 py-1.5 text-xs font-semibold text-muted-foreground hover:bg-secondary"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={createMutation.isPending}
                  className="rounded-md bg-blue-600 px-4 py-1.5 text-xs font-semibold text-white hover:bg-blue-700 disabled:opacity-50"
                >
                  {createMutation.isPending ? "Creating..." : "Save Conference"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
