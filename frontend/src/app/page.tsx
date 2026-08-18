"use client";

import React from "react";
import Link from "next/link";
import { useAuth } from "@/lib/auth";
import {
  ArrowRight,
  Sparkles,
  GitMerge,
  Scale,
  Network,
  ShieldCheck,
  CheckCircle2,
} from "lucide-react";
import type { UserRole } from "@/types";

export default function LandingPage() {
  const { quickLogin } = useAuth();

  return (
    <div className="relative min-h-screen w-full overflow-hidden bg-[#001726] text-white select-none">
      {/* Fullscreen Video Background */}
      <video
        autoPlay
        loop
        muted
        playsInline
        className="absolute inset-0 h-full w-full object-cover z-0 opacity-45 pointer-events-none"
      >
        <source
          src="https://d8j0ntlcm91z4.cloudfront.net/user_38xzZboKViGWJOttwIXH07lWA1P/hf_20260314_131748_f2ca2a28-fed7-44c8-b9a9-bd9acdd5ec31.mp4"
          type="video/mp4"
        />
      </video>

      {/* Subtle Cinematic Vignette Overlay */}
      <div className="absolute inset-0 z-0 bg-gradient-to-b from-[#001726]/60 via-transparent to-[#001726]/90 pointer-events-none" />

      {/* Navigation Bar */}
      <header className="relative z-10 mx-auto flex max-w-7xl items-center justify-between px-8 py-6">
        {/* Logo */}
        <Link href="/" className="group flex items-baseline gap-1 text-white">
          <span
            className="text-3xl tracking-tight text-white transition-opacity group-hover:opacity-90"
            style={{ fontFamily: "'Instrument Serif', serif" }}
          >
            AllocFlow
          </span>
          <sup className="text-xs text-muted-foreground">®</sup>
        </Link>

        {/* Nav links (hidden on mobile, visible on md+) */}
        <nav className="hidden md:flex items-center gap-8 text-sm text-muted-foreground">
          <Link href="/dashboard" className="text-white font-medium transition-colors hover:text-white">
            Operations
          </Link>
          <Link href="/dashboard/matching" className="transition-colors hover:text-white">
            Matching Cockpit
          </Link>
          <Link href="/dashboard/comparison" className="transition-colors hover:text-white">
            Tri-Algorithm Lab
          </Link>
          <Link href="/dashboard/graph-view" className="transition-colors hover:text-white">
            Graph Visualizer
          </Link>
          <Link href="/dashboard/experiments" className="transition-colors hover:text-white">
            Scalability
          </Link>
        </nav>

        {/* CTA button */}
        <Link
          href="/dashboard"
          className="liquid-glass rounded-full px-6 py-2.5 text-sm text-white transition-transform hover:scale-[1.03]"
        >
          Begin Journey
        </Link>
      </header>

      {/* Hero Section */}
      <main className="relative z-10 mx-auto flex max-w-7xl flex-col items-center justify-center px-6 pt-24 pb-32 text-center sm:pt-32 sm:pb-40">
        {/* Category Pill */}
        <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-white/10 bg-white/[0.02] px-4 py-1.5 text-xs text-muted-foreground backdrop-blur-md">
          <Sparkles className="h-3.5 w-3.5 text-white/80" />
          <span className="tracking-wide">Pure Java 21 Max-Flow Engine · Zero-COI Guarantees</span>
        </div>

        {/* Cinematic H1 */}
        <h1
          className="animate-fade-rise max-w-5xl text-5xl font-normal leading-[0.95] tracking-[-2.46px] text-white sm:text-7xl md:text-8xl"
          style={{ fontFamily: "'Instrument Serif', serif" }}
        >
          Where algorithms find{" "}
          <em className="not-italic text-muted-foreground">
            equilibrium through the flow.
          </em>
        </h1>

        {/* Subtext */}
        <p className="animate-fade-rise-delay mx-auto mt-8 max-w-2xl text-base leading-relaxed text-muted-foreground sm:text-lg">
          We&apos;re designing mathematical precision for academic research, conference chairs, and scholarly consensus. Amid the complexity of peer allocation, we build deterministic flow networks for exact matching and zero conflict.
        </p>

        {/* Hero CTA Action Group */}
        <div className="animate-fade-rise-delay-2 mt-12 flex flex-wrap items-center justify-center gap-4">
          <Link
            href="/dashboard/matching"
            className="liquid-glass flex items-center gap-3 rounded-full px-12 py-4 text-base font-medium text-white transition-transform hover:scale-[1.03] cursor-pointer"
          >
            <GitMerge className="h-4 w-4" />
            <span>Launch Matching Cockpit</span>
            <ArrowRight className="h-4 w-4 opacity-70" />
          </Link>

          <Link
            href="/dashboard/comparison"
            className="liquid-glass flex items-center gap-2 rounded-full px-8 py-4 text-sm font-medium text-muted-foreground hover:text-white transition-colors cursor-pointer"
          >
            <Scale className="h-4 w-4 text-purple-300" />
            <span>Tri-Algorithm Lab</span>
          </Link>
        </div>

        {/* Demo Roles Quick Login Pill Strip */}
        <div className="mt-16 w-full max-w-3xl liquid-glass rounded-2xl p-5 text-left space-y-3">
          <div className="flex items-center justify-between text-xs text-muted-foreground">
            <span className="font-semibold uppercase tracking-wider text-white/70">
              Instant Exploration with Seeded Demo Roles:
            </span>
            <span className="font-mono text-[11px] text-emerald-400 flex items-center gap-1">
              <CheckCircle2 className="h-3 w-3" /> FF == EK == Dinic Invariant Verified
            </span>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-4 gap-2.5">
            <Link
              href="/dashboard"
              onClick={() => quickLogin("SUPER_ADMIN")}
              className="rounded-xl border border-white/5 bg-white/[0.02] p-3 hover:bg-white/[0.08] hover:border-white/20 transition-all text-left group"
            >
              <p className="text-xs font-semibold text-white group-hover:text-blue-300">System Admin</p>
              <p className="text-[10px] text-muted-foreground truncate">admin@allocflow.io</p>
            </Link>

            <Link
              href="/dashboard"
              onClick={() => quickLogin("CONFERENCE_ADMIN")}
              className="rounded-xl border border-white/5 bg-white/[0.02] p-3 hover:bg-white/[0.08] hover:border-white/20 transition-all text-left group"
            >
              <p className="text-xs font-semibold text-white group-hover:text-purple-300">Conference Chair</p>
              <p className="text-[10px] text-muted-foreground truncate">chair@icdcs2026.org</p>
            </Link>

            <Link
              href="/dashboard/reviewers"
              onClick={() => quickLogin("REVIEWER")}
              className="rounded-xl border border-white/5 bg-white/[0.02] p-3 hover:bg-white/[0.08] hover:border-white/20 transition-all text-left group"
            >
              <p className="text-xs font-semibold text-white group-hover:text-emerald-300">PC Reviewer</p>
              <p className="text-[10px] text-muted-foreground truncate">reviewer.chen@stanford.edu</p>
            </Link>

            <Link
              href="/dashboard/manuscripts"
              onClick={() => quickLogin("AUTHOR")}
              className="rounded-xl border border-white/5 bg-white/[0.02] p-3 hover:bg-white/[0.08] hover:border-white/20 transition-all text-left group"
            >
              <p className="text-xs font-semibold text-white group-hover:text-amber-300">Author</p>
              <p className="text-[10px] text-muted-foreground truncate">author.vaswani@google.com</p>
            </Link>
          </div>
        </div>

        {/* Feature Cards in Liquid Glass */}
        <div className="mt-14 grid w-full max-w-5xl grid-cols-1 gap-4 sm:grid-cols-3 text-left">
          <div className="liquid-glass rounded-2xl p-6 space-y-2">
            <div className="rounded-full bg-white/10 p-2 text-white w-fit">
              <Network className="h-4 w-4" />
            </div>
            <h3 className="font-bold text-sm text-white" style={{ fontFamily: "'Instrument Serif', serif", fontSize: "1.25rem" }}>
              S → P → R → T Graph Network
            </h3>
            <p className="text-xs text-muted-foreground leading-relaxed">
              Canonical bipartite flow network with bounded reviewer capacities, paper review quotas, and strict zero-COI exclusion cuts.
            </p>
          </div>

          <div className="liquid-glass rounded-2xl p-6 space-y-2">
            <div className="rounded-full bg-white/10 p-2 text-white w-fit">
              <Scale className="h-4 w-4 text-purple-300" />
            </div>
            <h3 className="font-bold text-sm text-white" style={{ fontFamily: "'Instrument Serif', serif", fontSize: "1.25rem" }}>
              Tri-Algorithm Equivalence
            </h3>
            <p className="text-xs text-muted-foreground leading-relaxed">
              Deterministic comparison of Ford-Fulkerson, Edmonds-Karp, and Dinic algorithms with SHA-256 graph fingerprints and median/p95 latency.
            </p>
          </div>

          <div className="liquid-glass rounded-2xl p-6 space-y-2">
            <div className="rounded-full bg-white/10 p-2 text-white w-fit">
              <ShieldCheck className="h-4 w-4 text-emerald-300" />
            </div>
            <h3 className="font-bold text-sm text-white" style={{ fontFamily: "'Instrument Serif', serif", fontSize: "1.25rem" }}>
              Explainable Allocation Proofs
            </h3>
            <p className="text-xs text-muted-foreground leading-relaxed">
              Full transparency for every assigned edge: topic overlaps, keyword breakdown, capacity headroom, and mathematical provenance.
            </p>
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="relative z-10 border-t border-white/10 bg-[#001726]/60 backdrop-blur-md px-8 py-5 flex flex-wrap items-center justify-between text-xs text-muted-foreground">
        <span>AllocFlow® &middot; Enterprise Reviewer Assignment &amp; Manuscript Allocation Platform</span>
        <span className="font-mono text-[11px] text-white/60">Pure Java 21 DSA Engine + Spring Boot 3 + Next.js 14</span>
      </footer>
    </div>
  );
}
