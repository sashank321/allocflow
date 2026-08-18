"use client";

import React, { useState } from "react";
import { Navbar } from "@/components/layout/Navbar";
import { Sidebar } from "@/components/layout/Sidebar";

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const [conferenceCode, setConferenceCode] = useState("ICDCS-2026");

  return (
    <div className="min-h-screen bg-background flex flex-col">
      <Navbar
        activeConferenceCode={conferenceCode}
        onConferenceChange={setConferenceCode}
      />
      <div className="flex flex-1">
        <Sidebar />
        <main className="flex-1 p-6 overflow-y-auto max-h-[calc(100vh-4rem)]">
          {children}
        </main>
      </div>
    </div>
  );
}
