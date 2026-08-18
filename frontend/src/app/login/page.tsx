"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/lib/auth";
import { Lock, Mail, ArrowRight, CheckCircle2 } from "lucide-react";
import Link from "next/link";
import type { UserRole } from "@/types";

export default function LoginPage() {
  const router = useRouter();
  const { login, quickLogin, isLoading } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    try {
      await login(email, password);
      router.push("/dashboard");
    } catch (err: any) {
      setError(err.response?.data?.message || "Invalid email or password");
    }
  };

  const handleDemoLogin = async (role: UserRole) => {
    try {
      await quickLogin(role);
      router.push("/dashboard");
    } catch (err: any) {
      setError("Failed to sign in with demo account");
    }
  };

  return (
    <div className="relative min-h-screen w-full overflow-hidden bg-[#001726] text-white flex flex-col justify-center items-center p-6 select-none">
      {/* Fullscreen Video Background */}
      <video
        autoPlay
        loop
        muted
        playsInline
        className="absolute inset-0 h-full w-full object-cover z-0 opacity-35 pointer-events-none"
      >
        <source
          src="https://d8j0ntlcm91z4.cloudfront.net/user_38xzZboKViGWJOttwIXH07lWA1P/hf_20260314_131748_f2ca2a28-fed7-44c8-b9a9-bd9acdd5ec31.mp4"
          type="video/mp4"
        />
      </video>

      {/* Cinematic Gradient Overlay */}
      <div className="absolute inset-0 z-0 bg-gradient-to-b from-[#001726]/80 via-[#001726]/60 to-[#001726]/95 pointer-events-none" />

      <div className="relative z-10 w-full max-w-md space-y-6">
        {/* Header */}
        <div className="flex flex-col items-center text-center space-y-2">
          <Link href="/" className="group flex items-baseline gap-1 text-white">
            <span
              className="text-4xl tracking-tight text-white transition-opacity group-hover:opacity-90"
              style={{ fontFamily: "'Instrument Serif', serif" }}
            >
              AllocFlow
            </span>
            <sup className="text-xs text-muted-foreground">®</sup>
          </Link>
          <p className="text-xs text-muted-foreground">
            Academic Conference Reviewer Allocation Platform
          </p>
        </div>

        {/* Liquid Glass Form Card */}
        <div className="liquid-glass rounded-2xl p-7 shadow-2xl space-y-5 border border-white/10">
          {error && (
            <div className="rounded-lg bg-rose-500/20 border border-rose-500/30 p-2.5 text-xs text-rose-200 font-medium">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4 text-xs">
            <div>
              <label className="font-medium text-white/90">Email Address</label>
              <div className="relative mt-1.5">
                <Mail className="absolute left-3.5 top-2.5 h-4 w-4 text-muted-foreground" />
                <input
                  type="email"
                  required
                  placeholder="admin@allocflow.io"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="w-full rounded-lg border border-white/10 bg-white/[0.04] py-2.5 pl-10 pr-3 text-xs text-white placeholder:text-muted-foreground focus:border-white/30 focus:outline-none backdrop-blur-md"
                />
              </div>
            </div>

            <div>
              <label className="font-medium text-white/90">Password</label>
              <div className="relative mt-1.5">
                <Lock className="absolute left-3.5 top-2.5 h-4 w-4 text-muted-foreground" />
                <input
                  type="password"
                  required
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full rounded-lg border border-white/10 bg-white/[0.04] py-2.5 pl-10 pr-3 text-xs text-white placeholder:text-muted-foreground focus:border-white/30 focus:outline-none backdrop-blur-md"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="w-full liquid-glass rounded-lg py-2.5 text-xs font-semibold text-white transition-transform hover:scale-[1.02] disabled:opacity-50 mt-2"
            >
              {isLoading ? "Signing In..." : "Sign In with Credentials"}
            </button>
          </form>

          {/* Quick Demo Access */}
          <div className="pt-4 border-t border-white/10 space-y-2.5">
            <p className="text-[11px] font-bold uppercase tracking-wider text-muted-foreground text-center">
              Or Instant Demo Role Access:
            </p>
            <div className="grid grid-cols-2 gap-2 text-xs">
              <button
                type="button"
                onClick={() => handleDemoLogin("SUPER_ADMIN")}
                className="rounded-lg border border-white/5 bg-white/[0.03] p-2.5 text-left hover:bg-white/[0.08] hover:border-white/20 transition-all"
              >
                <p className="font-semibold text-white">System Admin</p>
                <p className="text-[10px] text-muted-foreground">Full privileges</p>
              </button>

              <button
                type="button"
                onClick={() => handleDemoLogin("CONFERENCE_ADMIN")}
                className="rounded-lg border border-white/5 bg-white/[0.03] p-2.5 text-left hover:bg-white/[0.08] hover:border-white/20 transition-all"
              >
                <p className="font-semibold text-white">Conference Chair</p>
                <p className="text-[10px] text-muted-foreground">Manage matching</p>
              </button>

              <button
                type="button"
                onClick={() => handleDemoLogin("REVIEWER")}
                className="rounded-lg border border-white/5 bg-white/[0.03] p-2.5 text-left hover:bg-white/[0.08] hover:border-white/20 transition-all"
              >
                <p className="font-semibold text-white">PC Reviewer</p>
                <p className="text-[10px] text-muted-foreground">Reviewer profile</p>
              </button>

              <button
                type="button"
                onClick={() => handleDemoLogin("AUTHOR")}
                className="rounded-lg border border-white/5 bg-white/[0.03] p-2.5 text-left hover:bg-white/[0.08] hover:border-white/20 transition-all"
              >
                <p className="font-semibold text-white">Author</p>
                <p className="text-[10px] text-muted-foreground">Submit papers</p>
              </button>
            </div>
          </div>
        </div>

        {/* Back Link */}
        <div className="text-center">
          <Link href="/" className="text-xs text-muted-foreground hover:text-white transition-colors">
            &larr; Back to AllocFlow Home
          </Link>
        </div>
      </div>
    </div>
  );
}
