"use client";

import React from "react";
import { useQuery } from "@tanstack/react-query";
import { api } from "@/lib/api";
import {
  FileText,
  Users,
  GitMerge,
  ShieldCheck,
  TrendingUp,
  AlertTriangle,
  Clock,
  ArrowUpRight,
  Sparkles,
  Zap,
  Activity,
  Layers,
} from "lucide-react";
import Link from "next/link";
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  PieChart,
  Pie,
  Cell,
} from "recharts";
import { Card3D } from "@/components/ui/Card3D";

export default function DashboardOverviewPage() {
  const [mounted, setMounted] = React.useState(false);

  React.useEffect(() => {
    setMounted(true);
  }, []);

  const { data: stats, isLoading } = useQuery({
    queryKey: ["dashboard-stats"],
    queryFn: () => api.getDashboardStats(),
  });

  const { data: auditLogs } = useQuery({
    queryKey: ["recent-audit-logs"],
    queryFn: () => api.getRecentAuditLogs(),
  });

  if (!mounted || isLoading || !stats) {
    return (
      <div className="flex h-96 items-center justify-center">
        <div className="flex flex-col items-center gap-3 text-muted-foreground">
          <div className="h-8 w-8 animate-spin rounded-full border-2 border-white border-t-transparent shadow-[0_0_15px_rgba(255,255,255,0.3)]" />
          <p className="text-xs text-white/80">Loading conference analytics...</p>
        </div>
      </div>
    );
  }

  // Format workload data for chart
  const workloadData = Object.entries(stats.reviewerWorkloadDistribution || {}).map(
    ([name, count]) => ({
      name: name.split(" ")[0] || name,
      count,
    })
  );

  // Format status data for pie chart
  const statusData = Object.entries(stats.manuscriptsByStatus || {}).map(
    ([status, count]) => ({
      name: status.replace("_", " "),
      value: count,
    })
  );

  const STATUS_COLORS = ["#FFFFFF", "#10B981", "#8B5CF6", "#F59E0B", "#94A3B8"];

  return (
    <div className="space-y-6 select-none text-white">
      {/* Header Banner */}
      <div className="glass-panel p-6 flex flex-wrap items-center justify-between gap-4 border border-white/10">
        <div>
          <div className="flex items-center gap-3">
            <h1
              className="text-3xl tracking-tight text-white"
              style={{ fontFamily: "'Instrument Serif', serif" }}
            >
              Conference Operations Overview
            </h1>
            <span className="rounded-full bg-emerald-500/20 border border-emerald-500/40 px-3 py-0.5 text-[11px] font-bold text-emerald-300 shadow-[0_0_15px_rgba(16,185,129,0.2)]">
              Live Allocation
            </span>
          </div>
          <p className="mt-1 text-xs text-muted-foreground">
            {stats.activeConferenceName} ({stats.activeConferenceCode}) • Network Flow Engine Active
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Link
            href="/dashboard/matching"
            className="liquid-glass rounded-xl px-4 py-2 text-xs font-semibold text-white flex items-center gap-2"
          >
            <GitMerge className="h-4 w-4 text-white" />
            <span>Launch Matching Cockpit</span>
          </Link>
          <Link
            href="/dashboard/comparison"
            className="btn-3d rounded-xl px-4 py-2 text-xs font-semibold text-white flex items-center gap-2"
          >
            <Sparkles className="h-4 w-4 text-purple-300" />
            <span>Tri-Algorithm Lab</span>
          </Link>
        </div>
      </div>

      {/* 3D KPI STATS GRID */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {/* Total Manuscripts */}
        <Card3D glowColor="white" className="p-5">
          <div className="flex items-center justify-between">
            <p className="text-xs font-medium text-muted-foreground uppercase tracking-wider">
              Total Manuscripts
            </p>
            <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-white/10 border border-white/15 text-white shadow-md">
              <FileText className="h-5 w-5" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-bold tracking-tight text-white">
              {stats.totalManuscripts}
            </span>
            <span className="text-xs text-muted-foreground font-medium">Papers</span>
          </div>
          <p className="mt-1.5 text-[11px] text-muted-foreground">
            Requiring {stats.totalRequiredReviews} total assigned reviews
          </p>
        </Card3D>

        {/* Reviewer Roster */}
        <Card3D glowColor="purple" className="p-5">
          <div className="flex items-center justify-between">
            <p className="text-xs font-medium text-muted-foreground uppercase tracking-wider">
              PC Reviewers
            </p>
            <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-purple-500/20 border border-purple-500/40 text-purple-300 shadow-md">
              <Users className="h-5 w-5" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-bold tracking-tight text-white">
              {stats.totalReviewers}
            </span>
            <span className="text-xs text-purple-300 font-medium">Experts</span>
          </div>
          <p className="mt-1.5 text-[11px] text-muted-foreground">
            Aggregate capacity: {stats.totalReviewerCapacity} slots available
          </p>
        </Card3D>

        {/* Allocated Pairs */}
        <Card3D glowColor="emerald" className="p-5">
          <div className="flex items-center justify-between">
            <p className="text-xs font-medium text-muted-foreground uppercase tracking-wider">
              Committed Matches
            </p>
            <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 shadow-md">
              <GitMerge className="h-5 w-5" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-bold tracking-tight text-white">
              {stats.totalAssignments}
            </span>
            <span className="text-xs text-emerald-400 font-medium">Pairs</span>
          </div>
          <p className="mt-1.5 text-[11px] text-muted-foreground">
            {(stats.averageCoveragePercentage ?? 0).toFixed(1)}% review requirement fulfilled
          </p>
        </Card3D>

        {/* COI Safeguards */}
        <Card3D glowColor="amber" className="p-5">
          <div className="flex items-center justify-between">
            <p className="text-xs font-medium text-muted-foreground uppercase tracking-wider">
              Conflict Rules
            </p>
            <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-amber-500/20 border border-amber-500/40 text-amber-300 shadow-md">
              <ShieldCheck className="h-5 w-5" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-bold tracking-tight text-white">
              {stats.totalConflicts}
            </span>
            <span className="text-xs text-amber-300 font-medium">COIs Enforced</span>
          </div>
          <p className="mt-1.5 text-[11px] text-muted-foreground">
            Zero conflict violations across flow graph
          </p>
        </Card3D>
      </div>

      {/* 3D CHARTS SECTION */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        {/* Workload Saturation Bar Chart */}
        <Card3D glowColor="purple" className="p-5 lg:col-span-2 space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-sm font-bold text-white">Reviewer Workload Saturation</h2>
              <p className="text-xs text-muted-foreground">Assigned papers per committee member</p>
            </div>
            <span className="rounded-md bg-white/10 px-2 py-0.5 text-[10px] font-medium text-white border border-white/20">
              Max Cap: 4
            </span>
          </div>

          <div className="h-64 w-full pt-4">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={workloadData} margin={{ top: 10, right: 10, left: -20, bottom: 20 }}>
                <XAxis
                  dataKey="name"
                  tickLine={false}
                  axisLine={{ stroke: "rgba(255,255,255,0.15)" }}
                  tick={{ fontSize: 11, fill: "#94A3B8" }}
                />
                <YAxis
                  allowDecimals={false}
                  tickLine={false}
                  axisLine={{ stroke: "rgba(255,255,255,0.15)" }}
                  tick={{ fontSize: 11, fill: "#94A3B8" }}
                />
                <Tooltip
                  contentStyle={{
                    backgroundColor: "rgba(10, 10, 10, 0.95)",
                    backdropFilter: "blur(12px)",
                    borderColor: "rgba(255, 255, 255, 0.15)",
                    borderRadius: "12px",
                    color: "#FFFFFF",
                    fontSize: "12px",
                    boxShadow: "0 10px 30px rgba(0,0,0,0.9)",
                  }}
                />
                <Bar dataKey="count" fill="#8B5CF6" radius={[6, 6, 0, 0]} barSize={28} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </Card3D>

        {/* Pipeline Distribution Pie */}
        <Card3D glowColor="white" className="p-5 space-y-4">
          <div>
            <h2 className="text-sm font-bold text-white">Manuscript Pipeline</h2>
            <p className="text-xs text-muted-foreground">Current status distribution</p>
          </div>

          <div className="h-64 w-full flex items-center justify-center">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={statusData}
                  cx="50%"
                  cy="50%"
                  innerRadius={50}
                  outerRadius={75}
                  paddingAngle={5}
                  dataKey="value"
                >
                  {statusData.map((entry, index) => (
                    <Cell
                      key={`cell-${index}`}
                      fill={STATUS_COLORS[index % STATUS_COLORS.length]}
                      stroke="rgba(0,0,0,0.7)"
                      strokeWidth={2}
                    />
                  ))}
                </Pie>
                <Tooltip
                  contentStyle={{
                    backgroundColor: "rgba(10, 10, 10, 0.95)",
                    backdropFilter: "blur(12px)",
                    borderColor: "rgba(255, 255, 255, 0.15)",
                    borderRadius: "12px",
                    color: "#FFFFFF",
                    fontSize: "12px",
                    boxShadow: "0 10px 30px rgba(0,0,0,0.9)",
                  }}
                />
              </PieChart>
            </ResponsiveContainer>
          </div>

          <div className="flex flex-wrap justify-center gap-2 pt-2 border-t border-white/10">
            {statusData.map((entry, i) => (
              <div key={i} className="flex items-center gap-1.5 text-[11px] text-muted-foreground">
                <div
                  className="h-2 w-2 rounded-full"
                  style={{ backgroundColor: STATUS_COLORS[i % STATUS_COLORS.length] }}
                />
                <span>
                  {entry.name}: <strong className="text-white">{entry.value}</strong>
                </span>
              </div>
            ))}
          </div>
        </Card3D>
      </div>

      {/* RECENT AUDIT TRAIL */}
      <Card3D glowColor="none" className="p-5 space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Clock className="h-4 w-4 text-white" />
            <h2 className="text-sm font-bold text-white">Immutable Audit Trail</h2>
          </div>
          <Link
            href="/dashboard/audit"
            className="text-xs text-white hover:underline flex items-center gap-1 transition-colors"
          >
            <span>View Full Audit Log</span>
            <ArrowUpRight className="h-3.5 w-3.5" />
          </Link>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-white/10 text-[11px] font-bold uppercase tracking-wider text-muted-foreground">
                <th className="pb-2.5">Timestamp</th>
                <th className="pb-2.5">Actor</th>
                <th className="pb-2.5">Action</th>
                <th className="pb-2.5">Target Entity</th>
                <th className="pb-2.5">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-white/5 font-mono text-[11px]">
              {(Array.isArray(auditLogs) ? auditLogs : []).slice(0, 5).map((log) => (
                <tr key={log.id} className="hover:bg-white/[0.03] transition-colors">
                  <td className="py-2.5 text-muted-foreground">
                    {new Date(log.timestamp).toLocaleTimeString()}
                  </td>
                  <td className="py-2.5 text-white font-sans">{log.actorEmail}</td>
                  <td className="py-2.5">
                    <span className="rounded-md bg-white/10 px-2 py-0.5 text-[10px] text-white border border-white/10">
                      {log.action}
                    </span>
                  </td>
                  <td className="py-2.5 text-muted-foreground">
                    {log.entityType} ({log.entityId ? log.entityId.substring(0, 8) : "—"})
                  </td>
                  <td className="py-2.5">
                    <span className="rounded bg-emerald-500/20 border border-emerald-500/30 px-1.5 py-0.5 text-[9px] font-bold text-emerald-300">
                      SUCCESS
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card3D>
    </div>
  );
}
