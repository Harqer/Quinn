import React from 'react';
import { createRoot } from 'react-dom/client';
import { initializeApp } from "firebase/app";
import { getAuth, GoogleAuthProvider } from "firebase/auth";
import { getFirestore } from "firebase/firestore";
import { getDatabase } from "firebase/database";
import { initializeAppCheck, ReCaptchaEnterpriseProvider } from "firebase/app-check";
import firebaseConfig from "./firebase-applet-config.json";
import { App } from "@/web/App";
import "@/web/i18n";
import "./index.css";
import { AppProvider } from "@/web/contexts/AppContext";
import { PlayerProvider } from "@/web/contexts/PlayerContext";
import * as Sentry from "@sentry/browser";

// Initialize Firebase
const firebaseApp = initializeApp(firebaseConfig);
const auth = getAuth(firebaseApp);
const db = getFirestore(firebaseApp);
const rtdb = getDatabase(firebaseApp);

// Initialize Sentry for error tracking
if (import.meta.env.PROD && import.meta.env.VITE_SENTRY_DSN) {
  Sentry.init({
    dsn: import.meta.env.VITE_SENTRY_DSN,
    integrations: [
      Sentry.browserTracingIntegration(),
      Sentry.replayIntegration(),
    ],
    tracesSampleRate: 1.0,
    replaysSessionSampleRate: 0.1,
    replaysOnErrorSampleRate: 1.0,
  });
} else if (import.meta.env.PROD) {
  console.warn("Sentry DSN not found in environment. Error tracking is disabled.");
}

// Initialize App Check
if (window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1") {
  (window as any).FIREBASE_APPCHECK_DEBUG_TOKEN = true;
}
try {
  const appCheckKey = import.meta.env.VITE_APP_CHECK_KEY || (window as any).VITE_APP_CHECK_KEY;
  if (!appCheckKey) {
    console.warn("App Check key not found. Firebase requests may be rejected in production.");
  } else {
    initializeAppCheck(firebaseApp, {
      provider: new ReCaptchaEnterpriseProvider(appCheckKey),
      isTokenAutoRefreshEnabled: true,
    });
  }
} catch (e) {
  console.error("App Check failed to initialize", e);
}

const container = document.getElementById('root');
if (container) {
  const root = createRoot(container);
  root.render(
    <React.StrictMode>
      <AppProvider>
        <PlayerProvider>
          <App />
        </PlayerProvider>
      </AppProvider>
    </React.StrictMode>
  );
}
