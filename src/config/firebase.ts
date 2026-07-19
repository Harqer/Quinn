import { initializeApp, getApps } from "firebase-admin/app";
import { getFirestore, FieldValue } from "firebase-admin/firestore";
import { getAuth } from "firebase-admin/auth";
import { getAppCheck } from "firebase-admin/app-check";
import { getDatabase } from "firebase-admin/database";
import firebaseConfig from "../../firebase-applet-config.json" with { type: "json" };

const databaseURL = process.env.FIREBASE_DATABASE_URL || `https://${firebaseConfig.projectId}-default-rtdb.firebaseio.com`;

if (!getApps().length) {
  initializeApp({
    projectId: firebaseConfig.projectId,
    databaseURL: databaseURL,
  });
}

export const db = getFirestore();
export const auth = getAuth();
export const appCheck = getAppCheck();
export const rtdb = getDatabase();
export { firebaseConfig, FieldValue };
