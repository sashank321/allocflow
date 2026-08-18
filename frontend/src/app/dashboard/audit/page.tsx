"use client";

import React, { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { api } from "@/lib/api";
import { ShieldAlert, Search, RefreshCw, Clock, Filter } from "lucide-react";

export default function AuditLogsPage() {
  const [searchTerm, setSearchTerm] = useState("");

  const { data: auditData, isLoading, refetch } = useQuery({
    queryKey: ["audit-logs"],
    queryFn: () => api.getAuditLogs(0, 50),
  });

  const logs = auditData?.content || [];

  const filteredLogs = logs.filter((log) => {
    return (
      (log.actorEmail && log.actorEmail.toLowerCase().includes(searchTerm.toLowerCase())) ||
      log.action.toLowerCase().includes(searchTerm.toLowerCase()) ||
      log.entityType.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (log.details && log.details.toLowerCase().includes(searchTerm.toLowerCase()))
    );
  });

  return (
    <div className="space-y-6 select-none text-white">
      {/* Header */}
      <div className="glass-panel p-6 flex flex-wrap items-center justify-between gap-4 border border-white/10">
        <div>
          <h1
            className="text-3xl tracking-tight text-white"
            style={{ fontFamily: "'Instrument Serif', serif" }}
          >
            Immutable Audit Trail
          </h1>
          <p className="mt-1 text-xs text-muted-foreground">
            Verifiable compliance log for reviewer assignments, overrides, and security events
          </p>
        </div>

        <button
          onClick={() => refetch()}
          className="btn-3d flex items-center gap-1.5 rounded-xl px-4 py-2 text-xs font-semibold text-white transition-colors"
        >
          <RefreshCw className="h-3.5 w-3.5" />
          <span>Refresh Logs</span>
        </button>
      </div>

      {/* Search Input */}
      <div className="relative w-full max-w-sm">
        <Search className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
        <input
          type="text"
          placeholder="Filter by actor, action, or details..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full rounded-xl border border-white/10 bg-black/60 py-2.5 pl-9 pr-4 text-xs text-white placeholder:text-muted-foreground focus:border-white/30 focus:outline-none"
        />
      </div>

      {/* Audit Log Table */}
      <div className="glass-panel overflow-hidden border border-white/10">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-white/10 bg-white/[0.02] text-[11px] font-bold uppercase tracking-wider text-muted-foreground">
                <th className="py-3 px-4">Timestamp (UTC)</th>
                <th className="py-3 px-4">Actor</th>
                <th className="py-3 px-4">Action</th>
                <th className="py-3 px-4">Target Entity</th>
                <th className="py-3 px-4">IP Address</th>
                <th className="py-3 px-4">Audit Details</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-white/5 font-mono">
              {isLoading ? (
                <tr>
                  <td colSpan={6} className="py-8 text-center text-muted-foreground font-sans">
                    Loading audit trail...
                  </td>
                </tr>
              ) : filteredLogs.length > 0 ? (
                filteredLogs.map((log) => (
                  <tr key={log.id} className="hover:bg-white/[0.03] transition-colors">
                    <td className="py-3 px-4 text-muted-foreground text-[11px]">
                      {new Date(log.timestamp).toISOString().replace("T", " ").substring(0, 19)}
                    </td>
                    <td className="py-3 px-4 font-sans font-medium text-white">
                      {log.actorEmail || "ANONYMOUS"}
                    </td>
                    <td className="py-3 px-4">
                      <span className="rounded-md bg-white/10 border border-white/15 px-2 py-0.5 text-[10px] font-bold text-white">
                        {log.action}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-muted-foreground text-[11px]">
                      {log.entityType}
                    </td>
                    <td className="py-3 px-4 text-muted-foreground text-[11px]">
                      {log.ipAddress || "127.0.0.1"}
                    </td>
                    <td className="py-3 px-4 font-sans text-muted-foreground text-[11px] max-w-md">
                      {log.details}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={6} className="py-8 text-center text-muted-foreground font-sans">
                    No audit records found matching your filter.
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
