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

export default function DashboardOverviewPage() {
  const { data: stats, isLoading } = useQuery({
    queryKey: ["dashboard-stats"],
    queryFn: () => api.getDashboardStats(),
  });

  const { data: auditLogs } = useQuery({
    queryKey: ["recent-audit-logs"],
    queryFn: () => api.getRecentAuditLogs(),
  });

  if (isLoading || !stats) {
    return (
      <div className="flex h-96 items-center justify-center">
        <div className="flex flex-col items-center gap-2 text-muted-foreground">
          <div className="h-6 w-6 animate-spin rounded-full border-2 border-blue-600 border-t-transparent" />
          <p className="text-xs">Loading conference analytics...</p>
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

  const STATUS_COLORS = ["#3b82f6", "#10b981", "#8b5cf6", "#f59e0b", "#ec4899"];

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="flex flex-wrap items-center justify-between gap-4 border-b pb-4">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-xl font-bold tracking-tight text-foreground">
              Conference Operations Overview
            </h1>
            <span className="rounded bg-emerald-50 border border-emerald-200 px-2 py-0.5 text-[11px] font-semibold text-emerald-800">
              Live Allocation System
            </span>
          </div>
          <p className="text-xs text-muted-foreground">
            {stats.activeConferenceName} ({stats.activeConferenceCode})
          </p>
        </div>

        <div className="flex items-center gap-2.5">
          <Link
            href="/dashboard/matching"
            className="flex items-center gap-1.5 rounded-md bg-blue-600 px-3.5 py-2 text-xs font-semibold text-white shadow-sm hover:bg-blue-700 transition-colors"
          >
            <GitMerge className="h-4 w-4" />
            <span>Launch Matching Cockpit</span>
          </Link>
          <Link
            href="/dashboard/comparison"
            className="flex items-center gap-1.5 rounded-md border bg-secondary/50 px-3.5 py-2 text-xs font-semibold text-secondary-foreground hover:bg-secondary transition-colors"
          >
            <Sparkles className="h-4 w-4 text-purple-600" />
            <span>Tri-Algorithm Comparison</span>
          </Link>
        </div>
      </div>

      {/* KPI Cards Grid */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {/* Manuscripts Card */}
        <div className="rounded-xl border bg-card p-4 shadow-sm space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-muted-foreground">Manuscripts</span>
            <div className="rounded-md bg-blue-50 p-1.5 text-blue-600">
              <FileText className="h-4 w-4" />
            </div>
          </div>
          <div className="flex items-baseline justify-between">
            <span className="text-2xl font-bold text-foreground">{stats.totalManuscripts}</span>
            <span className="text-[11px] text-muted-foreground font-mono">
              Req: {stats.totalRequiredReviews} reviews
            </span>
          </div>
          <div className="text-[11px] text-muted-foreground">
            Across active tracks &amp; submission categories
          </div>
        </div>

        {/* Reviewers Card */}
        <div className="rounded-xl border bg-card p-4 shadow-sm space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-muted-foreground">Active Reviewers</span>
            <div className="rounded-md bg-purple-50 p-1.5 text-purple-600">
              <Users className="h-4 w-4" />
            </div>
          </div>
          <div className="flex items-baseline justify-between">
            <span className="text-2xl font-bold text-foreground">{stats.activeReviewersCount}</span>
            <span className="text-[11px] text-muted-foreground font-mono">
              Cap: {stats.totalReviewerCapacity} slots
            </span>
          </div>
          <div className="text-[11px] text-muted-foreground">
            Total registered: {stats.totalReviewers} reviewers
          </div>
        </div>

        {/* Allocation Coverage Card */}
        <div className="rounded-xl border bg-card p-4 shadow-sm space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-muted-foreground">Assignment Coverage</span>
            <div className="rounded-md bg-emerald-50 p-1.5 text-emerald-600">
              <TrendingUp className="h-4 w-4" />
            </div>
          </div>
          <div className="flex items-baseline justify-between">
            <span className="text-2xl font-bold text-foreground">
              {stats.averageCoveragePercentage.toFixed(1)}%
            </span>
            <span className="text-[11px] text-emerald-700 font-medium">
              {stats.totalAssignments} assigned
            </span>
          </div>
          <div className="h-1.5 w-full rounded-full bg-secondary overflow-hidden">
            <div
              className="h-full bg-emerald-600 rounded-full"
              style={{ width: `${Math.min(100, stats.averageCoveragePercentage)}%` }}
            />
          </div>
        </div>

        {/* Conflict Exclusions Card */}
        <div className="rounded-xl border bg-card p-4 shadow-sm space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-muted-foreground">Conflicts Handled</span>
            <div className="rounded-md bg-amber-50 p-1.5 text-amber-600">
              <ShieldCheck className="h-4 w-4" />
            </div>
          </div>
          <div className="flex items-baseline justify-between">
            <span className="text-2xl font-bold text-foreground">{stats.totalConflicts}</span>
            <span className="text-[11px] text-amber-700 font-medium">100% Excluded</span>
          </div>
          <div className="text-[11px] text-muted-foreground">
            Zero COI violations in flow network
          </div>
        </div>
      </div>

      {/* Analytics Charts Grid */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        {/* Reviewer Workload Chart */}
        <div className="rounded-xl border bg-card p-5 shadow-sm lg:col-span-2 space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-sm font-bold text-foreground">Reviewer Workload Distribution</h2>
              <p className="text-xs text-muted-foreground">Current assigned manuscripts per reviewer</p>
            </div>
            <Link
              href="/dashboard/reviewers"
              className="flex items-center gap-1 text-xs font-medium text-blue-600 hover:underline"
            >
              View Roster <ArrowUpRight className="h-3.5 w-3.5" />
            </Link>
          </div>

          <div className="h-60 w-full pt-2">
            {workloadData.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={workloadData}>
                  <XAxis dataKey="name" tick={{ fontSize: 11 }} />
                  <YAxis allowDecimals={false} tick={{ fontSize: 11 }} />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: "hsl(var(--card))",
                      borderColor: "hsl(var(--border))",
                      fontSize: "12px",
                      borderRadius: "6px",
                    }}
                  />
                  <Bar dataKey="count" fill="#3b82f6" radius={[4, 4, 0, 0]} name="Assigned Reviews" />
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <div className="flex h-full items-center justify-center text-xs text-muted-foreground">
                No active reviewer assignments recorded yet
              </div>
            )}
          </div>
        </div>

        {/* Manuscript Status Breakdown */}
        <div className="rounded-xl border bg-card p-5 shadow-sm space-y-4">
          <div>
            <h2 className="text-sm font-bold text-foreground">Submission Pipeline</h2>
            <p className="text-xs text-muted-foreground">Status distribution across papers</p>
          </div>

          <div className="h-48 w-full">
            {statusData.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={statusData}
                    dataKey="value"
                    nameKey="name"
                    cx="50%"
                    cy="50%"
                    outerRadius={65}
                    label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                    labelLine={false}
                  >
                    {statusData.map((entry, index) => (
                      <Cell
                        key={`cell-${index}`}
                        fill={STATUS_COLORS[index % STATUS_COLORS.length]}
                      />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            ) : (
              <div className="flex h-full items-center justify-center text-xs text-muted-foreground">
                No submissions in pipeline
              </div>
            )}
          </div>

          <div className="pt-2 border-t space-y-1 text-xs">
            <div className="flex justify-between text-muted-foreground">
              <span>Required Flow Target:</span>
              <span className="font-mono font-bold text-foreground">
                {stats.totalRequiredReviews} units
              </span>
            </div>
            <div className="flex justify-between text-muted-foreground">
              <span>Available Reviewer Capacity:</span>
              <span className="font-mono font-bold text-foreground">
                {stats.totalReviewerCapacity} slots
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Recent System Audit Events */}
      <div className="rounded-xl border bg-card p-5 shadow-sm space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Clock className="h-4 w-4 text-blue-600" />
            <h2 className="text-sm font-bold text-foreground">Recent Security &amp; Execution Audit Logs</h2>
          </div>
          <Link
            href="/dashboard/audit"
            className="flex items-center gap-1 text-xs font-medium text-blue-600 hover:underline"
          >
            All Audit Records <ArrowUpRight className="h-3.5 w-3.5" />
          </Link>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="border-b bg-secondary/30 text-muted-foreground">
              <tr>
                <th className="py-2 px-3">Timestamp</th>
                <th className="py-2 px-3">Actor</th>
                <th className="py-2 px-3">Action</th>
                <th className="py-2 px-3">Target Entity</th>
                <th className="py-2 px-3">Audit Details</th>
              </tr>
            </thead>
            <tbody className="divide-y">
              {auditLogs && auditLogs.length > 0 ? (
                auditLogs.slice(0, 5).map((log) => (
                  <tr key={log.id} className="hover:bg-secondary/20 transition-colors">
                    <td className="py-2.5 px-3 font-mono text-[11px] text-muted-foreground">
                      {new Date(log.timestamp).toLocaleTimeString()}
                    </td>
                    <td className="py-2.5 px-3 font-medium text-foreground">{log.actorEmail || "SYSTEM"}</td>
                    <td className="py-2.5 px-3">
                      <span className="rounded bg-secondary px-1.5 py-0.5 font-mono text-[10px] font-semibold text-foreground">
                        {log.action}
                      </span>
                    </td>
                    <td className="py-2.5 px-3 text-muted-foreground">
                      {log.entityType} ({log.entityId ? log.entityId.substring(0, 8) : "N/A"})
                    </td>
                    <td className="py-2.5 px-3 text-muted-foreground">{log.details}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={5} className="py-4 text-center text-muted-foreground">
                    No recent audit events recorded.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
