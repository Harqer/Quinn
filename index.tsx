import React from 'react';
import { createRoot } from 'react-dom/client';
import { initializeApp } from "firebase/app";
import { getAuth, GoogleAuthProvider } from "firebase/auth";
import { getFirestore } from "firebase/firestore";
import { initializeAppCheck, ReCaptchaEnterpriseProvider } from "firebase/app-check";
import firebaseConfig from "@/firebase-applet-config.json";
import { App } from "@/web/App";
import "./index.css";

// Initialize Firebase
const firebaseApp = initializeApp(firebaseConfig);
const auth = getAuth(firebaseApp);
const db = getFirestore(firebaseApp);

// Initialize App Check
if (window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1") {
  (window as any).FIREBASE_APPCHECK_DEBUG_TOKEN = true;
}
try {
  initializeAppCheck(firebaseApp, {
    provider: new ReCaptchaEnterpriseProvider("6Ld_placeholder_recaptcha_enterprise_key"),
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
      <App />
    </React.StrictMode>
  );
}
