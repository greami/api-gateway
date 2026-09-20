import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

const apiProxyTarget = 'http://localhost:8080';

export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0',
    port: 5173,
    proxy: {
      '/auth': apiProxyTarget,
      '/utenti': apiProxyTarget,
      '/ruoli': apiProxyTarget,
      '/utente-ruoli': apiProxyTarget,
      '/eventi': apiProxyTarget,
      '/opere': apiProxyTarget,
      '/evento-opera': apiProxyTarget,
      '/associazioni': apiProxyTarget,
      '/prenotazioni': apiProxyTarget,
      '/feedback-eventi': apiProxyTarget,
      '/feedback-opere': apiProxyTarget,
    },
  },
});
