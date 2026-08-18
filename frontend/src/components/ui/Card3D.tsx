"use client";

import React from "react";
import GlassSurface from "./GlassSurface";

interface Card3DProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
  className?: string;
  glowColor?: "white" | "purple" | "emerald" | "amber" | "none";
  borderRadius?: number;
  backgroundOpacity?: number;
  saturation?: number;
}

export function Card3D({
  children,
  className = "",
  glowColor = "none",
  borderRadius = 16,
  backgroundOpacity = 0.5,
  saturation = 1.1,
  ...props
}: Card3DProps) {
  // Pure optical glass surface with zero hover tilt, zero neon slop, authentic refraction
  return (
    <GlassSurface
      borderRadius={borderRadius}
      backgroundOpacity={backgroundOpacity}
      saturation={saturation}
      borderWidth={0.06}
      brightness={30}
      opacity={0.88}
      blur={10}
      displace={0.4}
      distortionScale={-120}
      className={`glass-panel border border-white/10 ${className}`}
      style={{ width: "100%", height: "auto" }}
    >
      <div className="w-full h-full" {...props}>
        {children}
      </div>
    </GlassSurface>
  );
}

export { GlassSurface };
