import { db } from "../config/firebase.js";

export interface UserQuota {
  count: number;
  lastUpdated: string;
}

export class QuotaRepository {
  private collection = db.collection("user_quotas");

  async getQuota(uid: string): Promise<UserQuota | null> {
    const doc = await this.collection.doc(uid).get();
    if (!doc.exists) return null;
    return doc.data() as UserQuota;
  }

  async updateQuota(uid: string, quota: UserQuota): Promise<void> {
    await this.collection.doc(uid).set(quota);
  }
}

export const quotaRepository = new QuotaRepository();
