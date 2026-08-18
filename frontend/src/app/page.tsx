"use client";

import React from "react";
import Link from "next/link";
import {
  Layers,
  Sparkles,
  GitMerge,
  Scale,
  Network,
  FlaskConical,
  ShieldCheck,
  ArrowRight,
  CheckCircle2,
  Cpu,
  Fingerprint,
} from "lucide-react";
import { useAuth } from "@/lib/auth";

export default function LandingPage() {
  const { quickLogin } = useAuth();

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 text-foreground flex flex-col justify-between selection:bg-blue-100 selection:text-blue-900">
      {/* Header */}
      <header className="flex h-16 w-full items-center justify-between border-b bg-card/80 px-8 backdrop-blur-md sticky top-0 z-30">
        <div className="flex items-center gap-2.5">
          <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-blue-600 text-white shadow-sm shadow-blue-500/20">
            <Layers className="h-5 w-5" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="font-bold tracking-tight text-foreground">AllocFlow</span>
              <span className="rounded bg-blue-100 px-1.5 py-0.5 text-[10px] font-bold text-blue-800">
                DSA Engine
              </span>
            </div>
            <p className="text-[10px] text-muted-foreground">Academic Allocation System</p>
          </div>
        </div>

        <div className="flex items-center gap-4 text-xs font-semibold">
          <Link href="/dashboard" className="text-muted-foreground hover:text-foreground">
            Operations
          </Link>
          <Link href="/dashboard/comparison" className="text-muted-foreground hover:text-foreground">
            Research Lab
          </Link>
          <Link
            href="/dashboard"
            className="rounded-md bg-blue-600 px-3.5 py-1.5 text-white hover:bg-blue-700 shadow-sm transition-colors"
          >
            Launch Platform
          </Link>
        </div>
      </header>

      {/* Hero Section */}
      <main className="flex-1 flex flex-col items-center justify-center px-6 py-16 text-center max-w-5xl mx-auto space-y-8">
        <div className="inline-flex items-center gap-2 rounded-full border bg-card px-3.5 py-1 text-xs font-medium text-muted-foreground shadow-sm">
          <Sparkles className="h-3.5 w-3.5 text-blue-600" />
          <span>Deterministic Bipartite Flow Network &middot; Zero-COI Guarantees</span>
        </div>

        <div className="space-y-4 max-w-3xl">
          <h1 className="text-4xl sm:text-5xl font-extrabold tracking-tight text-slate-900 dark:text-white leading-tight">
            Enterprise Reviewer Assignment &amp; Manuscript Allocation Platform
          </h1>
          <p className="text-sm sm:text-base text-slate-600 dark:text-slate-400 leading-relaxed max-w-2xl mx-auto">
            A production-grade peer-review management engine backed by a pure Java 21 Maximum Flow engine. Evaluates and benchmarks Ford-Fulkerson, Edmonds-Karp, and Dinic algorithms with mathematical equivalence proofs.
          </p>
        </div>

        {/* CTA Buttons & Quick Demo Access */}
        <div className="flex flex-col sm:flex-row items-center gap-3 pt-2">
          <Link
            href="/dashboard/matching"
            className="flex items-center gap-2 rounded-lg bg-blue-600 px-5 py-2.5 text-sm font-semibold text-white shadow-md hover:bg-blue-700 transition-all"
          >
            <GitMerge className="h-4 w-4" />
            <span>Matching Engine Cockpit</span>
            <ArrowRight className="h-4 w-4" />
          </Link>

          <Link
            href="/dashboard/comparison"
            className="flex items-center gap-2 rounded-lg border bg-card px-5 py-2.5 text-sm font-semibold text-foreground shadow-sm hover:bg-secondary transition-all"
          >
            <Scale className="h-4 w-4 text-purple-600" />
            <span>Tri-Algorithm Comparison</span>
          </Link>
        </div>

        {/* Demo Roles Quick Login Grid */}
        <div className="w-full max-w-2xl rounded-xl border bg-card/60 backdrop-blur-sm p-4 text-left shadow-sm space-y-2">
          <p className="text-[11px] font-bold uppercase tracking-wider text-muted-foreground">
            Explore with Seeded Demo Roles:
          </p>
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
            <Link
              href="/dashboard"
              onClick={() => quickLogin("SUPER_ADMIN")}
              className="rounded-lg border bg-secondary/30 p-2.5 hover:bg-blue-50 hover:border-blue-300 transition-all group"
            >
              <p className="text-xs font-bold text-foreground group-hover:text-blue-700">System Admin</p>
              <p className="text-[10px] text-muted-foreground">admin@allocflow.io</p>
            </Link>

            <Link
              href="/dashboard"
              onClick={() => quickLogin("CONFERENCE_ADMIN")}
              className="rounded-lg border bg-secondary/30 p-2.5 hover:bg-purple-50 hover:border-purple-300 transition-all group"
            >
              <p className="text-xs font-bold text-foreground group-hover:text-purple-700">Conference Chair</p>
              <p className="text-[10px] text-muted-foreground">chair@icdcs2026.org</p>
            </Link>

            <Link
              href="/dashboard/reviewers"
              onClick={() => quickLogin("REVIEWER")}
              className="rounded-lg border bg-secondary/30 p-2.5 hover:bg-emerald-50 hover:border-emerald-300 transition-all group"
            >
              <p className="text-xs font-bold text-foreground group-hover:text-emerald-700">Reviewer (PC)</p>
              <p className="text-[10px] text-muted-foreground">reviewer.chen@stanford.edu</p>
            </Link>

            <Link
              href="/dashboard/manuscripts"
              onClick={() => quickLogin("AUTHOR")}
              className="rounded-lg border bg-secondary/30 p-2.5 hover:bg-amber-50 hover:border-amber-300 transition-all group"
            >
              <p className="text-xs font-bold text-foreground group-hover:text-amber-700">Author</p>
              <p className="text-[10px] text-muted-foreground">author.vaswani@google.com</p>
            </Link>
          </div>
        </div>

        {/* Feature Cards Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-5 text-left pt-6 w-full">
          <div className="rounded-xl border bg-card p-5 shadow-sm space-y-2">
            <div className="rounded-md bg-blue-50 p-2 text-blue-600 w-fit">
              <Network className="h-5 w-5" />
            </div>
            <h3 className="font-bold text-sm text-foreground">S → P → R → T Graph Engine</h3>
            <p className="text-xs text-muted-foreground leading-relaxed">
              Canonical bipartite flow network with bounded reviewer capacities, paper review requirements, and strict zero-COI exclusion cuts.
            </p>
          </div>

          <div className="rounded-xl border bg-card p-5 shadow-sm space-y-2">
            <div className="rounded-md bg-purple-50 p-2 text-purple-600 w-fit">
              <Scale className="h-5 w-5" />
            </div>
            <h3 className="font-bold text-sm text-foreground">Tri-Algorithm Equivalence</h3>
            <p className="text-xs text-muted-foreground leading-relaxed">
              Deterministic comparison of Ford-Fulkerson, Edmonds-Karp, and Dinic algorithms with SHA-256 graph fingerprints and median/p95 timings.
            </p>
          </div>

          <div className="rounded-xl border bg-card p-5 shadow-sm space-y-2">
            <div className="rounded-md bg-emerald-50 p-2 text-emerald-600 w-fit">
              <ShieldCheck className="h-5 w-5" />
            </div>
            <h3 className="font-bold text-sm text-foreground">Explainable Assignments</h3>
            <p className="text-xs text-muted-foreground leading-relaxed">
              Full explainability breakdown for every assigned edge: topic overlap, keyword matches, capacity headroom, and mathematical provenance.
            </p>
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="border-t bg-card px-8 py-4 flex flex-wrap items-center justify-between text-xs text-muted-foreground">
        <span>AllocFlow &middot; Production Reviewer Assignment &amp; Max-Flow Allocation System</span>
        <span className="font-mono text-[11px]">Java 21 DSA Engine + Spring Boot 3 + Next.js 14</span>
      </footer>
    </div>
  );
}
