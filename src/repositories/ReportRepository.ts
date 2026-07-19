import { db, FieldValue } from "../config/firebase.js";

export interface Report {
  id?: string;
  reporterId: string;
  targetId: string;
  targetType: "track" | "comment" | "user";
  reason: string;
  description?: string;
  createdAt?: any;
}

export class ReportRepository {
  private collection = db.collection("reports");

  async createReport(report: Report): Promise<string> {
    const docRef = await this.collection.add({
      ...report,
      createdAt: FieldValue.serverTimestamp(),
    });
    return docRef.id;
  }
}

export const reportRepository = new ReportRepository();
