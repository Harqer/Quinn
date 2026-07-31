import * as Sentry from "@sentry/browser";
import { logEvent } from "firebase/analytics";
import { analytics } from "./firebase";

const isProduction = process.env.NODE_ENV === "production";

export const logger = {
  info: (message: string, data?: any) => {
    if (!isProduction) {
      console.log(`[INFO] ${message}`, data || "");
    }
    // In production, info logs could go to a custom analytical endpoint or Sentry breadcrumbs
  },
  warn: (message: string, data?: any) => {
    if (!isProduction) {
      console.warn(`[WARN] ${message}`, data || "");
    }
    Sentry.captureMessage(message, { level: "warning", extra: data });
  },
  error: (message: string, error?: any) => {
    if (!isProduction) {
      console.error(`[ERROR] ${message}`, error || "");
    }
    if (error instanceof Error) {
      Sentry.captureException(error, { extra: { message } });
    } else {
      Sentry.captureMessage(`${message}: ${JSON.stringify(error)}`, { level: "error" });
    }
  },
  trackEvent: (eventName: string, params?: Record<string, any>) => {
    if (!isProduction) {
      console.log(`[EVENT] ${eventName}`, params || "");
    }
    if (analytics) {
      logEvent(analytics, eventName, params);
    }
  }
};
