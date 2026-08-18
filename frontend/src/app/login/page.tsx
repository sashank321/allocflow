"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/lib/auth";
import { Layers, Lock, Mail, ArrowRight, Sparkles, CheckCircle2 } from "lucide-react";
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
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 flex flex-col justify-center items-center p-4">
      <div className="w-full max-w-md space-y-6">
        {/* Logo */}
        <div className="flex flex-col items-center text-center space-y-2">
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-blue-600 text-white shadow-md shadow-blue-500/20">
            <Layers className="h-6 w-6" />
          </div>
          <h1 className="text-2xl font-bold tracking-tight text-foreground">Sign In to AllocFlow</h1>
          <p className="text-xs text-muted-foreground">
            Academic Conference Reviewer Allocation Platform
          </p>
        </div>

        {/* Form Card */}
        <div className="rounded-xl border bg-card p-6 shadow-sm space-y-4">
          {error && (
            <div className="rounded-md bg-rose-50 border border-rose-200 p-2.5 text-xs text-rose-700 font-medium">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-3.5 text-xs">
            <div>
              <label className="font-semibold text-foreground">Email Address</label>
              <div className="relative mt-1">
                <Mail className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
                <input
                  type="email"
                  required
                  placeholder="admin@allocflow.io"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="w-full rounded-md border bg-background py-2 pl-9 pr-3 text-xs focus:border-blue-500 focus:outline-none"
                />
              </div>
            </div>

            <div>
              <label className="font-semibold text-foreground">Password</label>
              <div className="relative mt-1">
                <Lock className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
                <input
                  type="password"
                  required
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full rounded-md border bg-background py-2 pl-9 pr-3 text-xs focus:border-blue-500 focus:outline-none"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="w-full rounded-md bg-blue-600 py-2 text-xs font-semibold text-white shadow-sm hover:bg-blue-700 disabled:opacity-50 transition-colors mt-2"
            >
              {isLoading ? "Signing In..." : "Sign In with Credentials"}
            </button>
          </form>

          {/* Quick Demo Logins */}
          <div className="pt-4 border-t space-y-2">
            <p className="text-[11px] font-bold uppercase tracking-wider text-muted-foreground text-center">
              Or Instant Demo Access:
            </p>
            <div className="grid grid-cols-2 gap-2 text-xs">
              <button
                type="button"
                onClick={() => handleDemoLogin("SUPER_ADMIN")}
                className="rounded border bg-secondary/30 p-2 text-left hover:bg-blue-50 hover:border-blue-300 transition-all"
              >
                <p className="font-bold text-foreground">System Admin</p>
                <p className="text-[10px] text-muted-foreground">Full privileges</p>
              </button>

              <button
                type="button"
                onClick={() => handleDemoLogin("CONFERENCE_ADMIN")}
                className="rounded border bg-secondary/30 p-2 text-left hover:bg-purple-50 hover:border-purple-300 transition-all"
              >
                <p className="font-bold text-foreground">Conference Chair</p>
                <p className="text-[10px] text-muted-foreground">Manage matching</p>
              </button>

              <button
                type="button"
                onClick={() => handleDemoLogin("REVIEWER")}
                className="rounded border bg-secondary/30 p-2 text-left hover:bg-emerald-50 hover:border-emerald-300 transition-all"
              >
                <p className="font-bold text-foreground">PC Reviewer</p>
                <p className="text-[10px] text-muted-foreground">Reviewer profile</p>
              </button>

              <button
                type="button"
                onClick={() => handleDemoLogin("AUTHOR")}
                className="rounded border bg-secondary/30 p-2 text-left hover:bg-amber-50 hover:border-amber-300 transition-all"
              >
                <p className="font-bold text-foreground">Author</p>
                <p className="text-[10px] text-muted-foreground">Submit papers</p>
              </button>
            </div>
          </div>
        </div>

        {/* Back Link */}
        <div className="text-center">
          <Link href="/" className="text-xs text-muted-foreground hover:text-foreground">
            &larr; Back to AllocFlow Home
          </Link>
        </div>
      </div>
    </div>
  );
}
