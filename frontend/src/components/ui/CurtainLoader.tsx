import React, { useEffect, useState } from "react";

export function CurtainLoader() {
  const [isMounted, setIsMounted] = useState(true);

  useEffect(() => {
    // Unmount the loader after animations complete to free up the DOM
    const timer = setTimeout(() => {
      setIsMounted(false);
    }, 2000);
    return () => clearTimeout(timer);
  }, []);

  if (!isMounted) return null;

  return (
    <div className="fixed inset-0 z-[9999] flex pointer-events-none overflow-hidden">
      {[0, 1, 2, 3, 4].map((i) => (
        <div
          key={i}
          className="h-full flex-1 bg-ink-black border-r border-white/5 origin-top"
          style={{
            animation: `curtain-wipe 1s cubic-bezier(0.77, 0, 0.175, 1) ${i * 0.1}s forwards`,
          }}
        />
      ))}
    </div>
  );
}
