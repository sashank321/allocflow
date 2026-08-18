"use client";

import React, { useState } from "react";
import { Navbar } from "@/components/layout/Navbar";
import { Sidebar } from "@/components/layout/Sidebar";
import PixelSnow from "@/components/ui/PixelSnow";

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const [conferenceCode, setConferenceCode] = useState("ICDCS-2026");

  return (
    <div className="relative min-h-screen bg-black text-white flex flex-col selection:bg-white/20 selection:text-white">
      {/* Background Pixel Snow Canvas */}
      <div className="fixed inset-0 pointer-events-none z-0">
        <PixelSnow
          color="#ffffff"
          flakeSize={0.01}
          minFlakeSize={1.25}
          pixelResolution={500}
          speed={0.3}
          density={0.35}
          direction={130}
          brightness={3}
          depthFade={11.5}
          farPlane={19}
          variant="snowflake"
        />
      </div>

      {/* Top Navbar */}
      <div className="relative z-20">
        <Navbar
          activeConferenceCode={conferenceCode}
          onConferenceChange={setConferenceCode}
        />
      </div>

      {/* Main Workspace with Sidebar */}
      <div className="relative z-10 flex flex-1 overflow-hidden">
        <Sidebar />
        <main className="flex-1 p-6 overflow-y-auto max-h-[calc(100vh-4rem)]">
          {children}
        </main>
      </div>
    </div>
  );
}
