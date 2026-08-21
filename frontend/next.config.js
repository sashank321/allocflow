/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  output: "standalone",
  async rewrites() {
    // Proxy to local backend or explicitly configured backend
    const backendUrl = process.env.BACKEND_INTERNAL_URL || "http://localhost:8080";
    const cleanUrl = backendUrl.replace(/\/api\/v1\/?$/, "");
    return [
      {
        source: "/api/v1/:path*",
        destination: `${cleanUrl}/api/v1/:path*`,
      },
    ];
  },
};

module.exports = nextConfig;
