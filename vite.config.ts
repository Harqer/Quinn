import path from 'path';
import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/postcss';

export default defineConfig(({ mode }) => {
    const env = loadEnv(mode, '.', '');
    const apiKey = process.env.GEMINI_API_KEY || env.GEMINI_API_KEY || '';

    return {
      server: {
        port: 3000,
        host: '0.0.0.0',
        hmr: false,
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
        react(),
        {
          name: 'inject-api-key',
          transformIndexHtml(html) {
            return html.replace(
              '<head>',
              `<head><script>
                window.process = {
                  env: {
                    API_KEY: ${JSON.stringify(apiKey)},
                    GEMINI_API_KEY: ${JSON.stringify(apiKey)}
                  },
                  versions: {
                    node: '22.0.0'
                  }
                };
                window.API_KEY = ${JSON.stringify(apiKey)};
                window.GEMINI_API_KEY = ${JSON.stringify(apiKey)};
              </script>`
            );
          }
        }
      ],
      css: {
        postcss: {
          plugins: [tailwindcss()],
        },
      },
      define: {
        'process.env.API_KEY': JSON.stringify(apiKey),
        'process.env.GEMINI_API_KEY': JSON.stringify(apiKey)
      }
    };
});
