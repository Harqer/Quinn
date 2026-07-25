import { initializeApp, getApps } from "firebase-admin/app";
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

export const auth = getAuth();
export const appCheck = getAppCheck();
export const rtdb = getDatabase();
export { firebaseConfig };

export function getRtdbShard(sessionId: string) {
  let hash = 0;
  for (let i = 0; i < sessionId.length; i++) {
    hash = sessionId.charCodeAt(i) + ((hash << 5) - hash);
  }
  const shardIndex = Math.abs(hash) % 3;
  
  if (shardIndex === 0) return rtdb;
  
  const shardName = `musically-studio-shard${shardIndex}`;
  const shardUrl = `https://${shardName}.firebaseio.com`;
  
  try {
    let shardApp = getApps().find(app => app.name === shardName);
    if (!shardApp) {
      shardApp = initializeApp({
        projectId: firebaseConfig.projectId,
        databaseURL: shardUrl,
      }, shardName);
    }
    return getDatabase(shardApp);
  } catch (e) {
    // Fallback to default if shard is missing in local/test
    return rtdb;
  }
}
