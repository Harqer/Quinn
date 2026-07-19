import { db } from "../config/firebase.js";
import { FieldValue } from "firebase-admin/firestore";

export class LogRepository {
  private gestureCollection = db.collection("gesture_logs");
  private batteryCollection = db.collection("battery_logs");

  async logGesture(gesture: string): Promise<string> {
    const expireAt = new Date();
    expireAt.setDate(expireAt.getDate() + 7);

    const docRef = await this.gestureCollection.add({
      gesture,
      timestamp: FieldValue.serverTimestamp(),
      expireAt,
    });
    return docRef.id;
  }

  async logBattery(batteryLevel: number, isWearDetected: string): Promise<string> {
    const expireAt = new Date();
    expireAt.setDate(expireAt.getDate() + 7);

    const docRef = await this.batteryCollection.add({
      batteryLevel,
      isWearDetected,
      timestamp: FieldValue.serverTimestamp(),
      expireAt,
    });
    return docRef.id;
  }

  async getRecentLogs(limit: number = 50) {
    const gesturesSnap = await this.gestureCollection.orderBy("timestamp", "desc").limit(limit).get();
    const batteriesSnap = await this.batteryCollection.orderBy("timestamp", "desc").limit(limit).get();

    return {
      gestures: gesturesSnap.docs.map(doc => ({ id: doc.id, ...doc.data() })),
      batteries: batteriesSnap.docs.map(doc => ({ id: doc.id, ...doc.data() })),
    };
  }
}

export const logRepository = new LogRepository();
