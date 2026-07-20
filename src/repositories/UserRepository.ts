import { db } from "../config/firebase.js";

export interface UserProfile {
  uid: string;
  email: string;
  name: string;
  birthday?: string;
  gender?: string;
  preferences?: string[];
  createdAt: any;
  updatedAt: any;
}

export class UserRepository {
  private collection = db.collection("users");

  async createProfile(uid: string, data: Partial<UserProfile>): Promise<void> {
    const docRef = this.collection.doc(uid);
    await docRef.set({
      ...data,
      uid,
      createdAt: new Date(),
      updatedAt: new Date(),
    }, { merge: true });
  }

  async updatePreferences(uid: string, artists: string[]): Promise<void> {
    const docRef = this.collection.doc(uid);
    await docRef.update({
      preferences: artists,
      updatedAt: new Date(),
    });
  }

  async getProfile(uid: string): Promise<UserProfile | null> {
    const doc = await this.collection.doc(uid).get();
    if (!doc.exists) return null;
    return doc.data() as UserProfile;
  }
}

export const userRepository = new UserRepository();
