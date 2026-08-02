import React from 'react';
import { createRoot } from 'react-dom/client';
import { App } from './App';
import '../../index.ts'; // Import index.ts to ensure Firebase and other globals are initialized

const rootElement = document.getElementById('root');
if (rootElement) {
  const root = createRoot(rootElement);
  root.render(
    <React.StrictMode>
      <App />
    </React.StrictMode>
  );
} else {
  console.error("Root element not found");
}
