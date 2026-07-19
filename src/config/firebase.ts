import { initializeApp, getApps } from "firebase-admin/app";
import { getFirestore, FieldValue } from "firebase-admin/firestore";
import { getAuth } from "firebase-admin/auth";
import { getAppCheck } from "firebase-admin/app-check";
import firebaseConfig from "../../firebase-applet-config.json" with { type: "json" };

if (!getApps().length) {
  initializeApp({
    projectId: firebaseConfig.projectId,
  });
}

export const db = getFirestore();
export const auth = getAuth();
export const appCheck = getAppCheck();
export { firebaseConfig, FieldValue };
