"use client";

import React, { useRef, useState } from "react";

interface Card3DProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
  className?: string;
  glowColor?: "white" | "purple" | "emerald" | "amber" | "none";
}

export function Card3D({
  children,
  className = "",
  glowColor = "none",
  ...props
}: Card3DProps) {
  const cardRef = useRef<HTMLDivElement>(null);
  const [rotateX, setRotateX] = useState(0);
  const [rotateY, setRotateY] = useState(0);
  const [glarePosition, setGlarePosition] = useState({ x: 50, y: 50 });
  const [isHovered, setIsHovered] = useState(false);

  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement>) => {
    if (!cardRef.current) return;
    const rect = cardRef.current.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    const centerX = rect.width / 2;
    const centerY = rect.height / 2;

    const rotX = -((y - centerY) / centerY) * 7;
    const rotY = ((x - centerX) / centerX) * 7;

    setRotateX(rotX);
    setRotateY(rotY);
    setGlarePosition({
      x: (x / rect.width) * 100,
      y: (y / rect.height) * 100,
    });
  };

  const handleMouseEnter = () => {
    setIsHovered(true);
  };

  const handleMouseLeave = () => {
    setIsHovered(false);
    setRotateX(0);
    setRotateY(0);
  };

  const getGlowStyle = () => {
    switch (glowColor) {
      case "white":
        return "hover:border-white/40 hover:shadow-[0_20px_50px_-10px_rgba(255,255,255,0.15)]";
      case "purple":
        return "hover:border-purple-500/40 hover:shadow-[0_20px_50px_-10px_rgba(168,85,247,0.25)]";
      case "emerald":
        return "hover:border-emerald-500/40 hover:shadow-[0_20px_50px_-10px_rgba(16,185,129,0.25)]";
      case "amber":
        return "hover:border-amber-500/40 hover:shadow-[0_20px_50px_-10px_rgba(245,158,11,0.25)]";
      default:
        return "hover:border-white/30 hover:shadow-[0_20px_50px_-10px_rgba(0,0,0,0.9)]";
    }
  };

  return (
    <div
      ref={cardRef}
      onMouseMove={handleMouseMove}
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
      style={{
        transform: isHovered
          ? `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) translateY(-4px)`
          : "perspective(1000px) rotateX(0deg) rotateY(0deg) translateY(0px)",
        transition: isHovered
          ? "transform 0.1s ease-out"
          : "transform 0.5s cubic-bezier(0.16, 1, 0.3, 1), box-shadow 0.5s ease",
      }}
      className={`glass-panel overflow-hidden transition-all duration-300 ${getGlowStyle()} ${className}`}
      {...props}
    >
      {/* Dynamic Specular Glare Highlight */}
      {isHovered && (
        <div
          className="pointer-events-none absolute inset-0 z-30 transition-opacity duration-300 opacity-60"
          style={{
            background: `radial-gradient(circle 320px at ${glarePosition.x}% ${glarePosition.y}%, rgba(255,255,255,0.12), transparent 70%)`,
          }}
        />
      )}
      <div className="relative z-10">{children}</div>
    </div>
  );
}
