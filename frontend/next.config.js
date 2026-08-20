/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  output: "standalone",
  async rewrites() {
    // Only apply rewrite if an explicit external remote backend is configured
    const backendUrl = process.env.BACKEND_INTERNAL_URL;
    if (backendUrl && !backendUrl.includes("localhost")) {
      const cleanUrl = backendUrl.replace(/\/api\/v1\/?$/, "");
      return [
        {
          source: "/api/v1/:path*",
          destination: `${cleanUrl}/api/v1/:path*`,
        },
      ];
    }
    return [];
  },
};

module.exports = nextConfig;
