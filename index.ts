/**
 * @fileoverview Generates real-time music based on a webcam feed.
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

// Register robust error catching to unpack and log the real error info before imports
window.addEventListener("error", (event) => {
  const msg = event.message || "";
  const filename = event.filename || "";
  if (
    msg.includes("Script error") || 
    filename.includes("html2canvas") || 
    filename.includes("jsdelivr") || 
    (filename && !filename.includes(window.location.origin) && !filename.startsWith("/"))
  ) {
    // Ignore third party script errors to prevent false-positive app failures
    return;
  }
  console.error("GLOBAL CLIENT-SIDE ERROR:", msg, "at", filename, ":", event.lineno, event.error);
});

window.addEventListener("unhandledrejection", (event) => {
  const reason = event.reason;
  const reasonStr = reason ? String(reason.message || reason) : "";
  if (
    reasonStr.includes("Script error") || 
    reasonStr.includes("html2canvas") ||
    reasonStr.includes("jsdelivr")
  ) {
    // Ignore third party rejection errors
    return;
  }
  console.error("GLOBAL CLIENT-SIDE UNHANDLED REJECTION:", event.reason);
});

import * as Sentry from "@sentry/browser";
import { initializeApp } from "firebase/app";
import { getAuth, GoogleAuthProvider, signInWithPopup, signOut, onAuthStateChanged, onIdTokenChanged } from "firebase/auth";
import { getFirestore, doc, onSnapshot, setDoc, collection } from "firebase/firestore";
import { initializeAppCheck, ReCaptchaEnterpriseProvider, getToken } from "firebase/app-check";
import firebaseConfig from "@/firebase-applet-config.json";

console.log("[STARTUP] index.ts loader triggered.");

// Initialize Firebase Client SDK
let appAuth: any = null;
try {
  console.log("[FIREBASE] Initializing Firebase Client SDK with project ID:", firebaseConfig.projectId);
  const firebaseApp = initializeApp(firebaseConfig);
  appAuth = getAuth(firebaseApp);
  (window as any).firebaseAuth = appAuth;
  (window as any).googleAuthProvider = new GoogleAuthProvider();
  (window as any).signInWithPopup = signInWithPopup;
  (window as any).signOut = signOut;
  (window as any).onAuthStateChanged = onAuthStateChanged;
  (window as any).onIdTokenChanged = onIdTokenChanged;

  // Configure Firestore Client SDK functions for real-time bidirectional syncing
  const db = getFirestore(firebaseApp);
  (window as any).firebaseDb = db;
  (window as any).firestoreDoc = doc;
  (window as any).firestoreOnSnapshot = onSnapshot;
  (window as any).firestoreSetDoc = setDoc;
  (window as any).firestoreCollection = collection;

  // Initialize App Check for client-side security
  try {
    const isDevelopment = window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1";
    if (isDevelopment) {
      (window as any).FIREBASE_APPCHECK_DEBUG_TOKEN = true;
    }
    const appCheck = initializeAppCheck(firebaseApp, {
      provider: new ReCaptchaEnterpriseProvider("6Ld_placeholder_recaptcha_enterprise_key"),
      isTokenAutoRefreshEnabled: true,
    });
    (window as any).firebaseAppCheck = appCheck;
    (window as any).firebaseAppCheckGetToken = getToken;
    console.log("[FIREBASE] App Check successfully configured.");
  } catch (appCheckErr) {
    console.warn("[FIREBASE] App Check skipped or inactive in this environment:", appCheckErr);
  }

  console.log("[FIREBASE] Firebase Client and Firestore SDK successfully configured.");
} catch (err) {
  console.error("[FIREBASE] Error initializing client-side Firebase Auth/Firestore:", err);
}

import { LyriaCamera } from "@/components/lyria_camera";
import { ToastMessage } from "@/components/toast_message";

if (!customElements.get("toast-message")) {
  console.log("[LIFECYCLE] Defining 'toast-message' custom element manually...");
  customElements.define("toast-message", ToastMessage);
}

if (!customElements.get("lyria-camera")) {
  console.log("[LIFECYCLE] Defining 'lyria-camera' custom element manually...");
  customElements.define("lyria-camera", LyriaCamera);
}

try {
  console.log("[SENTRY] Configuring client-side Sentry...");
  const integrations: any[] = [];
  if (typeof (Sentry as any).browserTracingIntegration === "function") {
    integrations.push((Sentry as any).browserTracingIntegration());
  }
  
  const dsn = (import.meta as any).env?.VITE_SENTRY_DSN || (window as any).process?.env?.SENTRY_DSN;
  if (dsn) {
    Sentry.init({
      dsn,
      integrations,
      tracesSampleRate: 1.0,
      ignoreErrors: [
        "Script error.",
        "Script error",
        "ResizeObserver loop limit exceeded",
        "ResizeObserver loop completed with undelivered notifications",
        "chrome-extension://",
        "moz-extension://",
      ],
      beforeSend(event) {
        // Sanitize any PII (authorization headers, tokens, user info, keys)
        if (event.request && event.request.headers) {
          delete event.request.headers["Authorization"];
          delete event.request.headers["authorization"];
          delete event.request.headers["Cookie"];
          delete event.request.headers["cookie"];
        }
        // Deep sanitize error messages & stack traces for API keys
        const sanitizeStr = (str: string): string => {
          return str.replace(/AQ\.[A-Za-z0-9_\-]+/g, "[REDACTED_API_KEY]")
                    .replace(/AIzaSy[A-Za-z0-9_\-]+/g, "[REDACTED_API_KEY]");
        };
        if (event.exception && event.exception.values) {
          event.exception.values.forEach((val) => {
            if (val.value) val.value = sanitizeStr(val.value);
            if (val.stacktrace && val.stacktrace.frames) {
              val.stacktrace.frames.forEach((frame) => {
                if (frame.filename) frame.filename = sanitizeStr(frame.filename);
                if (frame.function) frame.function = sanitizeStr(frame.function);
              });
            }
          });
        }
        return event;
      },
    });
    console.log("[SENTRY] Sentry client-side SDK successfully active.");
  } else {
    console.warn("[SENTRY] No SENTRY_DSN found. Client error reporting is inactive.");
  }
} catch (e) {
  console.error("[SENTRY] Failed to initialize Sentry safely:", e);
}

const init = async () => {
  console.log("[LIFECYCLE] Mave app bootstrap sequence initiated.");
  try {
    console.log("[LIFECYCLE] Checking custom elements registry status...");
    const isDefinedInitially = !!customElements.get("lyria-camera");
    console.log(`[LIFECYCLE] 'lyria-camera' defined in registry? ${isDefinedInitially}`);

    if (!isDefinedInitially) {
      console.log("[LIFECYCLE] 'lyria-camera' is not yet registered. Awaiting definition from component loader...");
    }

    // Await standard custom elements definition to ensure class registration complete before instantiation
    await customElements.whenDefined("lyria-camera");
    console.log("[LIFECYCLE] SUCCESS: 'lyria-camera' custom element class definition resolved in browser registry.");
    
    // Note: The React App mounts itself to #root in main.tsx.
    // We no longer append lyria-camera directly to the body here.
  } catch (e) {
    console.error("[LIFECYCLE] CRITICAL ERROR during custom element definition and mounting sequence:", e);
  }
};

init();



