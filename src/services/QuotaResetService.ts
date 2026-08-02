import { rtdb } from "../config/firebase.js";
import logger from "../config/logger.js";
import { getMonthKey } from "../middlewares/quota.js";

export class QuotaResetService {
  /**
   * Performs monthly quota reset and cleanup of expired usage records in RTDB.
   *
   * @param targetUid Optional specific user UID to reset; if omitted, scans all users.
   * @returns Summary of reset operations performed.
   */
  async runMonthlyReset(targetUid?: string): Promise<{ processedUsers: number; archivedMonths: number }> {
    const currentMonthKey = getMonthKey();
    let processedUsers = 0;
    let archivedMonths = 0;

    try {
      if (targetUid) {
        await this.resetUserUsage(targetUid, currentMonthKey);
        processedUsers = 1;
      } else {
        const usersSnapshot = await rtdb.ref("users").get();
        if (usersSnapshot.exists()) {
          const usersData = usersSnapshot.val();
          const uids = Object.keys(usersData);
          
          for (const uid of uids) {
            const count = await this.cleanupExpiredMonths(uid, currentMonthKey);
            await this.resetUserUsage(uid, currentMonthKey);
            archivedMonths += count;
            processedUsers++;
          }
        }
      }
      logger.info(`[QUOTA_RESET_JOB] Completed monthly reset job. Processed ${processedUsers} users, archived/cleaned ${archivedMonths} expired month records.`);
    } catch (err) {
      logger.error("[QUOTA_RESET_JOB] Failed to execute monthly quota reset:", { error: err });
      throw err;
    }

    return { processedUsers, archivedMonths };
  }

  /**
   * Ensures active month node exists for user and cleans up nodes older than 3 months.
   */
  private async cleanupExpiredMonths(uid: string, currentMonthKey: string): Promise<number> {
    let removedCount = 0;
    try {
      const usageSnapshot = await rtdb.ref(`users/${uid}/usage`).get();
      if (!usageSnapshot.exists()) return 0;

      const monthsData = usageSnapshot.val();
      const monthKeys = Object.keys(monthsData);

      // Parse year and month to calculate age
      const [currentYear, currentMonth] = currentMonthKey.split("-").map(Number);
      const currentAbsoluteMonth = currentYear * 12 + currentMonth;

      for (const monthKey of monthKeys) {
        const parts = monthKey.split("-");
        if (parts.length !== 2) continue;
        
        const [year, month] = parts.map(Number);
        if (isNaN(year) || isNaN(month)) continue;

        const absoluteMonth = year * 12 + month;
        // Archive/remove nodes older than 3 months
        if (currentAbsoluteMonth - absoluteMonth > 3) {
          await rtdb.ref(`users/${uid}/usage/${monthKey}`).remove();
          removedCount++;
        }
      }
    } catch (err) {
      logger.warn(`[QUOTA_RESET_JOB] Failed cleaning expired months for ${uid}:`, { error: err });
    }
    return removedCount;
  }

  /**
   * Resets active usage node for a specific user.
   */
  private async resetUserUsage(uid: string, monthKey: string): Promise<void> {
    await rtdb.ref(`users/${uid}/usage/${monthKey}`).set({
      songs_generated: 0,
      podcast_eps_generated: 0,
      realtime_minutes: 0,
      last_reset_at: new Date().toISOString()
    });
  }
}

export const quotaResetService = new QuotaResetService();
