"use client";

import React, { useState, useEffect } from "react";
import {
  Play,
  Pause,
  RotateCcw,
  ChevronRight,
  ChevronLeft,
  Info,
  Maximize2,
  Filter,
} from "lucide-react";
import type { GraphVisualization, GraphNode, GraphEdge } from "@/types";

interface BipartiteFlowGraphProps {
  data: GraphVisualization | null;
  traces?: string[];
  algorithmName?: string;
  onEdgeClick?: (manuscriptId: string, reviewerId: string) => void;
}

export function BipartiteFlowGraph({
  data,
  traces = [],
  algorithmName = "Dinic",
  onEdgeClick,
}: BipartiteFlowGraphProps) {
  const [selectedNode, setSelectedNode] = useState<string | null>(null);
  const [hoveredEdge, setHoveredEdge] = useState<GraphEdge | null>(null);
  const [currentStep, setCurrentStep] = useState<number>(0);
  const [isPlaying, setIsPlaying] = useState<boolean>(false);
  const [showOnlySaturated, setShowOnlySaturated] = useState<boolean>(false);

  // Playback timer for execution trace player
  useEffect(() => {
    let timer: NodeJS.Timeout;
    if (isPlaying && traces.length > 0) {
      timer = setInterval(() => {
        setCurrentStep((prev) => {
          if (prev >= traces.length - 1) {
            setIsPlaying(false);
            return prev;
          }
          return prev + 1;
        });
      }, 1200);
    }
    return () => clearInterval(timer);
  }, [isPlaying, traces.length]);

  if (!data || !data.nodes || data.nodes.length === 0) {
    return (
      <div className="flex h-80 w-full flex-col items-center justify-center rounded-xl border border-dashed bg-card/50 text-muted-foreground">
        <Info className="h-8 w-8 mb-2 opacity-50" />
        <p className="text-sm font-medium">No Flow Network Generated</p>
        <p className="text-xs text-muted-foreground/80">
          Run a matching simulation or benchmark to inspect the canonical bipartite network.
        </p>
      </div>
    );
  }

  // Partition nodes by column layer: Source (0), Manuscripts (1), Reviewers (2), Sink (3)
  const sourceNode = data.nodes.find((n) => n.type === "SOURCE");
  const manuscriptNodes = data.nodes.filter((n) => n.type === "MANUSCRIPT");
  const reviewerNodes = data.nodes.filter((n) => n.type === "REVIEWER");
  const sinkNode = data.nodes.find((n) => n.type === "SINK");

  const width = 900;
  const height = Math.max(480, Math.max(manuscriptNodes.length, reviewerNodes.length) * 55 + 60);

  const colX = {
    source: 80,
    manuscripts: 280,
    reviewers: 620,
    sink: 820,
  };

  // Compute node Y positions
  const nodePositions: Record<string, { x: number; y: number; node: GraphNode }> = {};

  if (sourceNode) {
    nodePositions[sourceNode.id] = { x: colX.source, y: height / 2, node: sourceNode };
  }

  const pGap = (height - 80) / Math.max(1, manuscriptNodes.length);
  manuscriptNodes.forEach((node, i) => {
    nodePositions[node.id] = {
      x: colX.manuscripts,
      y: 40 + i * pGap + pGap / 2,
      node,
    };
  });

  const rGap = (height - 80) / Math.max(1, reviewerNodes.length);
  reviewerNodes.forEach((node, i) => {
    nodePositions[node.id] = {
      x: colX.reviewers,
      y: 40 + i * rGap + rGap / 2,
      node,
    };
  });

  if (sinkNode) {
    nodePositions[sinkNode.id] = { x: colX.sink, y: height / 2, node: sinkNode };
  }

  const filteredEdges = showOnlySaturated
    ? data.edges.filter((e) => e.flow > 0)
    : data.edges;

  return (
    <div className="flex flex-col rounded-xl border bg-card shadow-sm overflow-hidden">
      {/* Visualizer Top Bar & Controls */}
      <div className="flex flex-wrap items-center justify-between border-b bg-secondary/30 px-4 py-2.5 gap-2 text-xs">
        <div className="flex items-center gap-3">
          <span className="font-bold text-foreground">Bipartite Flow Graph</span>
          <span className="rounded bg-blue-100 px-2 py-0.5 font-mono text-[10px] font-semibold text-blue-800">
            S → P → R → T
          </span>
          <span className="text-muted-foreground text-[11px]">
            {data.nodes.length} Vertices · {data.edges.length} Edges
          </span>
        </div>

        {/* Trace Player & Filter Toggles */}
        <div className="flex items-center gap-3">
          <label className="flex items-center gap-1.5 cursor-pointer text-[11px] text-muted-foreground select-none">
            <input
              type="checkbox"
              checked={showOnlySaturated}
              onChange={(e) => setShowOnlySaturated(e.target.checked)}
              className="rounded text-blue-600 focus:ring-0"
            />
            <span>Saturated Edges Only</span>
          </label>

          {traces.length > 0 && (
            <div className="flex items-center gap-1 border-l pl-3">
              <button
                onClick={() => setCurrentStep((p) => Math.max(0, p - 1))}
                disabled={currentStep === 0}
                className="p-1 text-muted-foreground hover:text-foreground disabled:opacity-30"
              >
                <ChevronLeft className="h-4 w-4" />
              </button>
              <button
                onClick={() => setIsPlaying(!isPlaying)}
                className="flex items-center gap-1 rounded bg-secondary px-2 py-1 text-[11px] font-semibold hover:bg-secondary/80"
              >
                {isPlaying ? <Pause className="h-3 w-3" /> : <Play className="h-3 w-3" />}
                <span>{isPlaying ? "Pause" : "Play Trace"}</span>
              </button>
              <button
                onClick={() => setCurrentStep((p) => Math.min(traces.length - 1, p + 1))}
                disabled={currentStep >= traces.length - 1}
                className="p-1 text-muted-foreground hover:text-foreground disabled:opacity-30"
              >
                <ChevronRight className="h-4 w-4" />
              </button>
              <button
                onClick={() => {
                  setIsPlaying(false);
                  setCurrentStep(0);
                }}
                className="p-1 text-muted-foreground hover:text-foreground"
                title="Reset Trace"
              >
                <RotateCcw className="h-3.5 w-3.5" />
              </button>
              <span className="font-mono text-[10px] text-muted-foreground pl-1">
                Step {currentStep + 1}/{traces.length}
              </span>
            </div>
          )}
        </div>
      </div>

      {/* Step Trace Banner */}
      {traces.length > 0 && traces[currentStep] && (
        <div className="bg-purple-50/80 border-b border-purple-100 px-4 py-1.5 text-xs text-purple-900 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="font-bold text-[11px] text-purple-700 uppercase">
              {algorithmName} Execution Step:
            </span>
            <span className="font-mono text-[11px] font-medium">{traces[currentStep]}</span>
          </div>
          <span className="text-[10px] text-purple-600">Click any saturated edge for explainability</span>
        </div>
      )}

      {/* Interactive SVG Canvas */}
      <div className="relative w-full overflow-x-auto p-4 flex justify-center bg-slate-50/50 dark:bg-slate-950/20">
        <svg width={width} height={height} className="select-none">
          <defs>
            <marker
              id="arrow-default"
              viewBox="0 0 10 10"
              refX="16"
              refY="5"
              markerWidth="6"
              markerHeight="6"
              orient="auto-start-reverse"
            >
              <path d="M 0 0 L 10 5 L 0 10 z" fill="#94a3b8" />
            </marker>
            <marker
              id="arrow-saturated"
              viewBox="0 0 10 10"
              refX="16"
              refY="5"
              markerWidth="6"
              markerHeight="6"
              orient="auto-start-reverse"
            >
              <path d="M 0 0 L 10 5 L 0 10 z" fill="#2563eb" />
            </marker>
          </defs>

          {/* Draw Edges */}
          {filteredEdges.map((edge, i) => {
            const p1 = nodePositions[edge.source];
            const p2 = nodePositions[edge.target];
            if (!p1 || !p2) return null;

            const isSaturated = edge.flow > 0;
            const isHighlighted =
              selectedNode && (edge.source === selectedNode || edge.target === selectedNode);

            // Bezier curve control points
            const dx = (p2.x - p1.x) / 2;
            const pathD = `M ${p1.x} ${p1.y} C ${p1.x + dx} ${p1.y}, ${p2.x - dx} ${p2.y}, ${p2.x} ${p2.y}`;

            const midX = (p1.x + p2.x) / 2;
            const midY = (p1.y + p2.y) / 2;

            return (
              <g
                key={`edge-${i}`}
                className="cursor-pointer group"
                onClick={() => {
                  if (edge.manuscriptId && edge.reviewerId && onEdgeClick) {
                    onEdgeClick(edge.manuscriptId, edge.reviewerId);
                  }
                }}
                onMouseEnter={() => setHoveredEdge(edge)}
                onMouseLeave={() => setHoveredEdge(null)}
              >
                {/* Background path for hover hit target */}
                <path d={pathD} fill="none" stroke="transparent" strokeWidth="12" />

                {/* Visible Edge Line */}
                <path
                  d={pathD}
                  fill="none"
                  stroke={
                    isHighlighted
                      ? "#2563eb"
                      : isSaturated
                      ? "#3b82f6"
                      : "#cbd5e1"
                  }
                  strokeWidth={isSaturated ? "2.5" : "1.2"}
                  strokeOpacity={isSaturated || isHighlighted ? 1 : 0.4}
                  className={isSaturated ? "flow-animate" : ""}
                  markerEnd={isSaturated ? "url(#arrow-saturated)" : "url(#arrow-default)"}
                />

                {/* Capacity / Flow Label */}
                {(isSaturated || isHighlighted) && (
                  <g transform={`translate(${midX}, ${midY})`}>
                    <rect
                      x="-14"
                      y="-8"
                      width="28"
                      height="16"
                      rx="3"
                      fill="#ffffff"
                      stroke={isSaturated ? "#3b82f6" : "#cbd5e1"}
                      strokeWidth="1"
                    />
                    <text
                      x="0"
                      y="3.5"
                      textAnchor="middle"
                      className="font-mono text-[9px] font-bold fill-slate-800"
                    >
                      {edge.flow}/{edge.capacity}
                    </text>
                  </g>
                )}
              </g>
            );
          })}

          {/* Draw Nodes */}
          {Object.entries(nodePositions).map(([id, { x, y, node }]) => {
            const isSelected = selectedNode === id;
            const isSourceSink = node.type === "SOURCE" || node.type === "SINK";

            let fill = "#ffffff";
            let stroke = "#94a3b8";
            let textFill = "#1e293b";

            if (node.type === "SOURCE") {
              fill = "#eff6ff";
              stroke = "#3b82f6";
            } else if (node.type === "SINK") {
              fill = "#fdf2f8";
              stroke = "#ec4899";
            } else if (node.type === "MANUSCRIPT") {
              fill = "#f0fdf4";
              stroke = "#22c55e";
            } else if (node.type === "REVIEWER") {
              fill = "#faf5ff";
              stroke = "#a855f7";
            }

            return (
              <g
                key={`node-${id}`}
                transform={`translate(${x}, ${y})`}
                className="cursor-pointer"
                onClick={() => setSelectedNode(selectedNode === id ? null : id)}
              >
                {/* Node Pill / Circle */}
                <rect
                  x="-55"
                  y="-16"
                  width="110"
                  height="32"
                  rx="6"
                  fill={fill}
                  stroke={isSelected ? "#2563eb" : stroke}
                  strokeWidth={isSelected ? "2.5" : "1.5"}
                  className="transition-all hover:scale-105 shadow-sm"
                />

                {/* Node Label */}
                <text
                  x="0"
                  y="-1"
                  textAnchor="middle"
                  className="font-sans text-[10px] font-bold fill-slate-900 pointer-events-none"
                >
                  {node.label}
                </text>

                {/* Capacity badge */}
                <text
                  x="0"
                  y="10"
                  textAnchor="middle"
                  className="font-mono text-[8.5px] font-medium fill-slate-500 pointer-events-none"
                >
                  Cap: {node.capacity}
                </text>
              </g>
            );
          })}
        </svg>
      </div>

      {/* Footer Legend */}
      <div className="flex items-center justify-between border-t bg-card px-4 py-2 text-[11px] text-muted-foreground">
        <div className="flex items-center gap-4">
          <span className="flex items-center gap-1.5">
            <span className="h-2.5 w-2.5 rounded-full bg-blue-500" /> Source (S)
          </span>
          <span className="flex items-center gap-1.5">
            <span className="h-2.5 w-2.5 rounded-full bg-emerald-500" /> Manuscripts (P)
          </span>
          <span className="flex items-center gap-1.5">
            <span className="h-2.5 w-2.5 rounded-full bg-purple-500" /> Reviewers (R)
          </span>
          <span className="flex items-center gap-1.5">
            <span className="h-2.5 w-2.5 rounded-full bg-pink-500" /> Sink (T)
          </span>
        </div>
        <span className="font-medium text-blue-600">
          Animated dashes = active residual flow
        </span>
      </div>
    </div>
  );
}
