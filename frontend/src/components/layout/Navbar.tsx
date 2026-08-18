"use client";

import React from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { useAuth } from "@/lib/auth";
import {
  Layers,
  Sparkles,
  UserCheck,
  LogOut,
  ChevronDown,
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
    <header className="sticky top-0 z-30 flex h-16 w-full items-center justify-between border-b bg-card/80 px-6 backdrop-blur-md">
      {/* Brand & Conference Badge */}
      <div className="flex items-center gap-6">
        <Link href="/dashboard" className="flex items-center gap-2.5">
          <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-blue-600 text-white shadow-sm shadow-blue-500/20">
            <Layers className="h-5 w-5" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="font-bold tracking-tight text-foreground">AllocFlow</span>
              <span className="rounded bg-blue-50 px-1.5 py-0.5 text-[10px] font-semibold text-blue-700">
                DSA Engine
              </span>
            </div>
            <p className="text-[11px] text-muted-foreground">Manuscript Allocation System</p>
          </div>
        </Link>

        <div className="hidden md:flex items-center gap-2 rounded-md border bg-secondary/50 px-3 py-1.5 text-xs text-secondary-foreground">
          <Building2 className="h-3.5 w-3.5 text-blue-600" />
          <span className="font-medium text-foreground">{activeConferenceCode}</span>
          <span className="text-[10px] text-muted-foreground">(Active Cycle)</span>
        </div>
      </div>

      {/* Mode Indicator & Quick Action Pills */}
      <div className="flex items-center gap-4">
        {/* Mode Badge */}
        <div
          className={`flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-semibold ${
            isResearchMode
              ? "bg-purple-50 text-purple-700 border border-purple-200"
              : "bg-blue-50 text-blue-700 border border-blue-200"
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
        <div className="hidden lg:flex items-center gap-1 rounded-lg border bg-secondary/30 p-1 text-xs">
          <span className="px-2 text-[11px] font-medium text-muted-foreground">Demo Role:</span>
          {(["SUPER_ADMIN", "CONFERENCE_ADMIN", "REVIEWER", "AUTHOR"] as UserRole[]).map((role) => (
            <button
              key={role}
              onClick={() => quickLogin(role)}
              className={`rounded px-2 py-0.5 text-[11px] transition-colors ${
                user?.role === role
                  ? "bg-card font-semibold text-blue-600 shadow-sm"
                  : "text-muted-foreground hover:text-foreground"
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
              <p className="text-xs font-medium leading-none text-foreground">{user?.fullName}</p>
              <p className="text-[10px] text-muted-foreground">{user?.email}</p>
            </div>
            <button
              onClick={logout}
              className="flex h-8 w-8 items-center justify-center rounded-md border text-muted-foreground hover:bg-secondary hover:text-foreground"
              title="Sign Out"
            >
              <LogOut className="h-4 w-4" />
            </button>
          </div>
        ) : (
          <Link
            href="/login"
            className="rounded-md bg-blue-600 px-3.5 py-1.5 text-xs font-semibold text-white hover:bg-blue-700"
          >
            Sign In
          </Link>
        )}
      </div>
    </header>
  );
}
