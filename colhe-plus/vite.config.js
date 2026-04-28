import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    // Proxy para o backend Spring Boot durante desenvolvimento
    // Isso evita problemas de CORS no dev
    proxy: {
      "/auth": "http://localhost:8080",
      "/lotes": "http://localhost:8080",
      "/pedidos": "http://localhost:8080",
      "/termo": "http://localhost:8080",
      "/admin": "http://localhost:8080",
    },
  },
});
