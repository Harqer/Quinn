import { HttpsError } from "firebase-functions/v2/https";
import { db } from "./firebase";
import { getAuth } from "firebase-admin/auth";
import { FieldValue } from "firebase-admin/firestore";

export async function checkFreeQuota(uid: string) {
  const userRecord = await getAuth().getUser(uid);
  const tier = (userRecord.customClaims?.subscriptionTier as String | undefined)?.toUpperCase() || 'FREE';

  if (tier === 'PREMIUM' || tier === 'FAMILY') {
    return;
  }

  const quotaRef = db.collection('user_quotas').doc(uid);

  // Security Fix: Transaction to prevent bypass via race conditions
  await db.runTransaction(async (transaction) => {
    const doc = await transaction.get(quotaRef);
    const generations = doc.exists ? doc.data()?.generations || 0 : 0;

    if (generations >= 1) {
      throw new HttpsError(
        "permission-denied",
        "Free tier limit reached. You have generated your 1 free episode. Please upgrade to the PREMIUM or FAMILY plan to continue creating unlimited high-quality content."
      );
    }

    transaction.set(quotaRef, { generations: FieldValue.increment(1) }, { merge: true });
  });
}
