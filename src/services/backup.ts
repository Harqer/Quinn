import { firebaseConfig } from "../config/firebase.js";
import logger from "../config/logger.js";

const isGcpEnvironment = process.env.NODE_ENV === "production" || !!process.env.K_SERVICE || !!process.env.GOOGLE_APPLICATION_CREDENTIALS;

export const triggerFirestoreAutoBackup = async () => {
  if (!isGcpEnvironment) {
    logger.info("[BACKUP] Skipping real Firestore auto-backup: non-production/local environment.");
    return { success: false, message: "Skipped in non-production/local environment" };
  }

  try {
    const projectId = firebaseConfig.projectId;
    if (!projectId) {
      logger.warn("[BACKUP] Missing projectId. Cannot trigger auto-backup.");
      return { success: false, message: "Missing GCP projectId" };
    }

    logger.info("[BACKUP] Initiating scheduled automated Firestore export...");

    const { v1 } = await import("@google-cloud/firestore");
    if (v1 && v1.FirestoreAdminClient) {
      const client = new v1.FirestoreAdminClient();
      const bucket = `gs://${projectId}-backups`;
      const databaseName = client.databasePath(projectId, "(default)");

      const [operation] = await client.exportDocuments({
        name: databaseName,
        outputUriPrefix: bucket,
        collectionIds: [], // Export all collections
      });

      logger.info(`[BACKUP] Backup operation successfully initiated.`, { operation: operation.name });
      return { success: true, operationName: operation.name };
    } else {
      logger.warn("[BACKUP] FirestoreAdminClient is not available in the current environment.");
      return { success: false, message: "FirestoreAdminClient not available" };
    }
  } catch (err: any) {
    logger.error("[BACKUP] Failed to trigger Firestore auto-backup:", { error: err.message || err });
    return { success: false, error: err.message || err };
  }
};

export const startBackupCron = () => {
  const TWENTY_FOUR_HOURS_MS = 24 * 60 * 60 * 1000;
  setInterval(() => {
    triggerFirestoreAutoBackup().catch((err) => logger.error("Backup Cron Failed:", { error: err }));
  }, TWENTY_FOUR_HOURS_MS);
};
