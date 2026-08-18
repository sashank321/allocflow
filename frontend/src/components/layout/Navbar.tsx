"use client";

import React from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { useAuth } from "@/lib/auth";
import {
  Layers,
  Sparkles,
  LogOut,
  Building2,
  Cpu,
  FlaskConical,
} from "lucide-react";
import type { UserRole } from "@/types";

interface NavbarProps {
  activeConferenceCode?: string;
  onConferenceChange?: (code: string) => void;
}

export function Navbar({ activeConferenceCode = "ICDCS-2026" }: NavbarProps) {
  const { user, logout, quickLogin, isAuthenticated } = useAuth();
  const pathname = usePathname();

  const isResearchMode =
    pathname.includes("/matching") ||
    pathname.includes("/comparison") ||
    pathname.includes("/experiments") ||
    pathname.includes("/graph-view");

  return (
    <header className="sticky top-0 z-30 flex h-16 w-full items-center justify-between border-b border-white/10 bg-black/60 px-6 backdrop-blur-md text-white">
      {/* Brand & Conference Badge */}
      <div className="flex items-center gap-6">
        <Link href="/dashboard" className="flex items-center gap-2.5 group">
          <span
            className="text-2xl tracking-tight text-white transition-opacity group-hover:opacity-90"
            style={{ fontFamily: "'Instrument Serif', serif" }}
          >
            AllocFlow
          </span>
          <sup className="text-xs text-muted-foreground">®</sup>
          <span className="rounded bg-white/10 px-1.5 py-0.5 text-[9px] font-bold text-white/80 border border-white/10">
            DSA Engine
          </span>
        </Link>

        <div className="hidden md:flex items-center gap-2 rounded-lg border border-white/10 bg-white/[0.03] px-3 py-1.5 text-xs text-muted-foreground">
          <Building2 className="h-3.5 w-3.5 text-blue-400" />
          <span className="font-medium text-white">{activeConferenceCode}</span>
          <span className="text-[10px] text-muted-foreground">(Active Cycle)</span>
        </div>
      </div>

      {/* Mode Indicator & Quick Action Pills */}
      <div className="flex items-center gap-4">
        {/* Mode Badge */}
        <div
          className={`flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-semibold backdrop-blur-md ${
            isResearchMode
              ? "bg-purple-500/20 text-purple-200 border border-purple-500/30"
              : "bg-blue-500/20 text-blue-200 border border-blue-500/30"
          }`}
        >
          {isResearchMode ? (
            <>
              <FlaskConical className="h-3.5 w-3.5" />
              <span>RESEARCH MODE</span>
            </>
          ) : (
            <>
              <Cpu className="h-3.5 w-3.5" />
              <span>OPERATIONS MODE</span>
            </>
          )}
        </div>

        {/* Demo Role Switcher */}
        <div className="hidden lg:flex items-center gap-1 rounded-lg border border-white/10 bg-white/[0.03] p-1 text-xs">
          <span className="px-2 text-[11px] font-medium text-muted-foreground">Demo:</span>
          {(["SUPER_ADMIN", "CONFERENCE_ADMIN", "REVIEWER", "AUTHOR"] as UserRole[]).map((role) => (
            <button
              key={role}
              onClick={() => quickLogin(role)}
              className={`rounded px-2 py-0.5 text-[11px] transition-all ${
                user?.role === role
                  ? "bg-white/20 font-semibold text-white shadow-sm border border-white/20"
                  : "text-muted-foreground hover:text-white"
              }`}
            >
              {role === "SUPER_ADMIN"
                ? "SysAdmin"
                : role === "CONFERENCE_ADMIN"
                ? "Chair"
                : role === "REVIEWER"
                ? "Reviewer"
                : "Author"}
            </button>
          ))}
        </div>

        {/* User Menu / Logout */}
        {isAuthenticated ? (
          <div className="flex items-center gap-3">
            <div className="text-right">
              <p className="text-xs font-medium leading-none text-white">{user?.fullName}</p>
              <p className="text-[10px] text-muted-foreground">{user?.email}</p>
            </div>
            <button
              onClick={logout}
              className="flex h-8 w-8 items-center justify-center rounded-lg border border-white/10 text-muted-foreground hover:bg-white/10 hover:text-white transition-colors"
              title="Sign Out"
            >
              <LogOut className="h-4 w-4" />
            </button>
          </div>
        ) : (
          <Link
            href="/login"
            className="liquid-glass rounded-full px-4 py-1.5 text-xs font-semibold text-white hover:scale-105 transition-all"
          >
            Sign In
          </Link>
        )}
      </div>
    </header>
  );
}
