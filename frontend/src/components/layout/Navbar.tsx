"use client";

import React from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { useAuth } from "@/lib/auth";
import { LogOut } from "lucide-react";
import type { UserRole } from "@/types";
import { BackendStatusPill } from "./BackendStatusPill";

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
    <header className="sticky top-0 z-30 flex h-16 w-full items-center justify-between border-b border-divider bg-ink-black/90 px-6 backdrop-blur-md text-beige-bg font-sans">
      {/* Brand & Conference Badge */}
      <div className="flex items-center gap-6">
        <Link href="/dashboard" className="flex items-center gap-2 group">
          <svg className="h-6 w-6" viewBox="0 0 32 32" fill="none">
            <circle cx="6" cy="16" r="4" fill="#E57D25"/>
            <circle cx="26" cy="16" r="4" fill="#E57D25"/>
            <path d="M6 16L26 16" stroke="#f4f1e6" strokeWidth="2"/>
          </svg>
          <span className="font-heading text-xl font-bold tracking-tight text-beige-bg transition-opacity group-hover:opacity-90">
            Alloc<span className="text-accent-orange font-normal italic">Flow</span>
          </span>
          <span className="ml-2 px-1.5 py-0.5 text-[9px] font-mono font-bold tracking-wider text-accent-orange border border-accent-orange/30 bg-accent-orange/10 uppercase">
            Portal
          </span>
        </Link>

        <div className="hidden md:flex items-center gap-2 px-3 py-1 font-space text-xs text-muted border-l border-divider">
          <span className="font-bold text-beige-bg uppercase tracking-widest">{activeConferenceCode}</span>
          <span className="text-[10px] opacity-60">{"// ACTIVE CYCLE"}</span>
        </div>
      </div>

      {/* Mode Indicator & Quick Action Pills */}
      <div className="flex items-center gap-4">
        <BackendStatusPill />

        {/* Terminal Mode Badge */}
        <div
          className={`font-mono text-[11px] font-bold tracking-widest uppercase px-2 py-1 ${
            isResearchMode
              ? "text-accent-blue bg-accent-blue/10 border border-accent-blue/20"
              : "text-beige-bg bg-white/5 border border-white/10"
          }`}
        >
          {isResearchMode ? "[ RSH_MODE ]" : "[ OPS_MODE ]"}
        </div>

        {/* Demo Role Switcher */}
        <div className="hidden lg:flex items-center gap-1 p-1 text-xs font-space border border-divider bg-black/40">
          <span className="px-2 text-[10px] uppercase text-muted tracking-widest">User:</span>
          {(["SUPER_ADMIN", "CONFERENCE_ADMIN", "REVIEWER", "AUTHOR"] as UserRole[]).map((role) => (
            <button
              key={role}
              onClick={() => quickLogin(role)}
              className={`px-2 py-0.5 text-[10px] uppercase tracking-wider transition-all ${
                user?.role === role
                  ? "bg-beige-bg text-ink-black font-bold"
                  : "text-muted hover:text-beige-bg"
              }`}
            >
              {role === "SUPER_ADMIN"
                ? "Sys"
                : role === "CONFERENCE_ADMIN"
                ? "Chair"
                : role === "REVIEWER"
                ? "Rev"
                : "Auth"}
            </button>
          ))}
        </div>

        {/* User Menu / Logout */}
        {isAuthenticated ? (
          <div className="flex items-center gap-3 pl-2 border-l border-divider">
            <div className="text-right font-space">
              <p className="text-[11px] font-bold uppercase text-beige-bg">{user?.fullName}</p>
              <p className="text-[9px] text-muted tracking-widest">{user?.email}</p>
            </div>
            <button
              onClick={logout}
              className="flex h-8 w-8 items-center justify-center border border-divider text-muted hover:bg-white/10 hover:text-beige-bg transition-colors"
              title="Sign Out"
            >
              <LogOut className="h-4 w-4" />
            </button>
          </div>
        ) : (
          <Link
            href="/login"
            className="px-4 py-1.5 font-space text-[11px] font-bold uppercase tracking-widest text-ink-black bg-accent-orange hover:bg-[#d67220] transition-colors"
          >
            Sign In
          </Link>
        )}
      </div>
    </header>
  );
}
