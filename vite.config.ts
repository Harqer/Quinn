import path from 'path';
import { defineConfig, loadEnv } from 'vite';


export default defineConfig(({ mode }) => {
    const env = loadEnv(mode, '.', '');
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
      plugins: [
        {
          name: 'inject-api-key',
          transformIndexHtml(html) {
            const apiKey = process.env.GEMINI_API_KEY || env.GEMINI_API_KEY || '';
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
      define: {
        'process.env.API_KEY': JSON.stringify(process.env.GEMINI_API_KEY || env.GEMINI_API_KEY || ''),
        'process.env.GEMINI_API_KEY': JSON.stringify(process.env.GEMINI_API_KEY || env.GEMINI_API_KEY || '')
      },
      resolve: {
        alias: {
          '@': path.resolve(__dirname, '.'),
        }
      }
    };
});
