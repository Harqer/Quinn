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
// Initialize Firebase
const firebaseApp = initializeApp(firebaseConfig);
const auth = getAuth(firebaseApp);
const db = getFirestore(firebaseApp);
const rtdb = getDatabase(firebaseApp);

// Initialize App Check
if (window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1") {
  (window as any).FIREBASE_APPCHECK_DEBUG_TOKEN = true;
}
try {
  const appCheckKey = (window as any).VITE_APP_CHECK_KEY || "6Ld_placeholder_recaptcha_enterprise_key";
  initializeAppCheck(firebaseApp, {
    provider: new ReCaptchaEnterpriseProvider(appCheckKey),
    isTokenAutoRefreshEnabled: true,
  });
} catch (e) {
  console.warn("App Check failed to initialize", e);
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
