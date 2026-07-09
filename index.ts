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
import { LyriaCamera } from "@/components/lyria_camera.ts";

try {
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
  }
} catch (e) {
  console.error("Failed to initialize Sentry safely:", e);
}

const init = () => {
  try {
    const cameraEl = document.createElement("lyria-camera");
    if (document.body) {
      document.body.appendChild(cameraEl);
    } else {
      document.addEventListener("DOMContentLoaded", () => {
        document.body.appendChild(cameraEl);
      });
    }
  } catch (e) {
    console.error("Failed to instantiate and append LyriaCamera component:", e);
  }
};

init();


