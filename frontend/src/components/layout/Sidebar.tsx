"use client";

import React from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  LayoutDashboard,
  Building2,
  FileText,
  Users,
  GitMerge,
  Scale,
  Network,
  FlaskConical,
  ShieldAlert,
  ChevronRight,
} from "lucide-react";

export function Sidebar() {
  const pathname = usePathname();

  const operationsNav = [
    { label: "Overview", href: "/dashboard", icon: LayoutDashboard },
    { label: "Conferences", href: "/dashboard/conferences", icon: Building2 },
    { label: "Manuscripts", href: "/dashboard/manuscripts", icon: FileText },
    { label: "Reviewers", href: "/dashboard/reviewers", icon: Users },
    { label: "Audit Logs", href: "/dashboard/audit", icon: ShieldAlert },
  ];

  const researchNav = [
    { label: "Matching Cockpit", href: "/dashboard/matching", icon: GitMerge, badge: "Max-Flow" },
    { label: "Algorithm Comparison", href: "/dashboard/comparison", icon: Scale, badge: "FF·EK·Dinic" },
    { label: "Flow Graph Visualizer", href: "/dashboard/graph-view", icon: Network, badge: "S→P→R→T" },
    { label: "Scalability Lab", href: "/dashboard/experiments", icon: FlaskConical, badge: "Sweeps" },
  ];

  return (
    <aside className="w-64 border-r bg-card flex flex-col justify-between p-4 h-[calc(100vh-4rem)] sticky top-16 select-none">
      <div className="space-y-6">
        {/* OPERATIONS SECTION */}
        <div>
          <p className="px-3 text-[11px] font-bold uppercase tracking-wider text-muted-foreground/70">
            Operations
          </p>
          <nav className="mt-2 space-y-1">
            {operationsNav.map((item) => {
              const active = pathname === item.href;
              const Icon = item.icon;
              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={`flex items-center justify-between rounded-lg px-3 py-2 text-xs font-medium transition-colors ${
                    active
                      ? "bg-blue-50 text-blue-700 font-semibold"
                      : "text-muted-foreground hover:bg-secondary hover:text-foreground"
                  }`}
                >
                  <div className="flex items-center gap-2.5">
                    <Icon className={`h-4 w-4 ${active ? "text-blue-600" : "text-muted-foreground"}`} />
                    <span>{item.label}</span>
                  </div>
                  {active && <ChevronRight className="h-3.5 w-3.5 text-blue-600" />}
                </Link>
              );
            })}
          </nav>
        </div>

        {/* RESEARCH LAB SECTION */}
        <div>
          <div className="flex items-center justify-between px-3">
            <p className="text-[11px] font-bold uppercase tracking-wider text-purple-700">
              Research Lab
            </p>
            <span className="rounded bg-purple-100 px-1.5 py-0.2 text-[9px] font-bold text-purple-800">
              DSA Suite
            </span>
          </div>
          <nav className="mt-2 space-y-1">
            {researchNav.map((item) => {
              const active = pathname === item.href;
              const Icon = item.icon;
              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={`flex items-center justify-between rounded-lg px-3 py-2 text-xs font-medium transition-colors ${
                    active
                      ? "bg-purple-50 text-purple-800 font-semibold border border-purple-200/60"
                      : "text-muted-foreground hover:bg-secondary hover:text-foreground"
                  }`}
                >
                  <div className="flex items-center gap-2.5">
                    <Icon className={`h-4 w-4 ${active ? "text-purple-700" : "text-muted-foreground"}`} />
                    <span>{item.label}</span>
                  </div>
                  {item.badge && (
                    <span className="rounded bg-secondary px-1.5 py-0.5 text-[9px] font-medium text-muted-foreground">
                      {item.badge}
                    </span>
                  )}
                </Link>
              );
            })}
          </nav>
        </div>
      </div>

      {/* System Invariant Status Footer */}
      <div className="rounded-lg border bg-secondary/30 p-3 text-[11px]">
        <div className="flex items-center justify-between">
          <span className="font-semibold text-foreground">Graph Invariant</span>
          <span className="rounded bg-emerald-100 px-1.5 py-0.5 text-[9px] font-bold text-emerald-800">
            ACTIVE
          </span>
        </div>
        <p className="mt-1 font-mono text-[10px] text-muted-foreground">
          FF.flow == EK.flow == Dinic.flow
        </p>
        <p className="mt-0.5 text-[10px] text-muted-foreground">
          Capacity &amp; Zero-COI strictly enforced.
        </p>
      </div>
    </aside>
  );
}
