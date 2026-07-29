import path from 'path';
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/postcss';

export default defineConfig(({ mode }) => {
    return {
      server: {
        port: 3000,
        host: '0.0.0.0',
        hmr: false,
        proxy: {
          '/api': 'http://localhost:8081'
        }
      },
      preview: {
        port: 3000,
        host: '0.0.0.0',
        allowedHosts: true,
      },
      resolve: {
        alias: {
          '@/web': path.resolve(__dirname, './src/web'),
          '@/ui': path.resolve(__dirname, './src/web/components/ui'),
          '@/layout': path.resolve(__dirname, './src/web/components/layout'),
          '@/features': path.resolve(__dirname, './src/web/features'),
          '@': path.resolve(__dirname, './src'),
        }
      },
      plugins: [
        react()
      ],
      css: {
        postcss: {
          plugins: [tailwindcss()],
        },
      }
    };
});
