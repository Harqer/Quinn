/**
 * @fileoverview Robust IndexedDB cache for offline logging queue and stream caching.
 */

export interface OfflineLog {
  id?: number;
  type: "gesture" | "battery" | "prompt";
  payload: any;
  timestamp: number;
}

export class OfflineCache {
  private db: IDBDatabase | null = null;
  private dbName = "QuinnOfflineCache";
  private dbVersion = 1;

  constructor() {
    this.initDatabase().catch(() => {});
  }

  private initDatabase(): Promise<IDBDatabase> {
    return new Promise((resolve, reject) => {
      if (typeof window === "undefined" || !window.indexedDB) {
        reject(new Error("IndexedDB not supported in this environment."));
        return;
      }

      const request = window.indexedDB.open(this.dbName, this.dbVersion);

      request.onerror = (event) => {
        console.error("[INDEXEDDB] Database failed to open:", event);
        reject(new Error("Database failed to open."));
      };

      request.onsuccess = (event) => {
        this.db = (event.target as IDBOpenDBRequest).result;
        console.log("[INDEXEDDB] Offline cache DB successfully active.");
        resolve(this.db);
      };

      request.onupgradeneeded = (event) => {
        const db = (event.target as IDBOpenDBRequest).result;
        if (!db.objectStoreNames.contains("pending_logs")) {
          db.createObjectStore("pending_logs", { keyPath: "id", autoIncrement: true });
          console.log("[INDEXEDDB] Created Object Store: pending_logs");
        }
      };
    });
  }

  public async addLog(type: "gesture" | "battery" | "prompt", payload: any): Promise<void> {
    try {
      const db = this.db || await this.initDatabase();
      return new Promise<void>((resolve, reject) => {
        const transaction = db.transaction(["pending_logs"], "readwrite");
        const store = transaction.objectStore("pending_logs");
        const record: OfflineLog = {
          type,
          payload,
          timestamp: Date.now()
        };
        const request = store.add(record);

        request.onsuccess = () => {
          console.log(`[INDEXEDDB] Offline log queued: [${type}]`);
          resolve();
        };

        request.onerror = (err) => {
          console.error("[INDEXEDDB] Failed to add offline log:", err);
          reject(err);
        };
      });
    } catch (e) {
      console.warn("[INDEXEDDB] Error during log caching fallback:", e);
    }
  }

  public async getPendingLogs(): Promise<OfflineLog[]> {
    try {
      const db = this.db || await this.initDatabase();
      return new Promise<OfflineLog[]>((resolve, reject) => {
        const transaction = db.transaction(["pending_logs"], "readonly");
        const store = transaction.objectStore("pending_logs");
        const request = store.getAll();

        request.onsuccess = () => {
          resolve(request.result || []);
        };

        request.onerror = (err) => {
          reject(err);
        };
      });
    } catch (e) {
      return [];
    }
  }

  public async deleteLog(id: number): Promise<void> {
    try {
      const db = this.db || await this.initDatabase();
      return new Promise<void>((resolve, reject) => {
        const transaction = db.transaction(["pending_logs"], "readwrite");
        const store = transaction.objectStore("pending_logs");
        const request = store.delete(id);

        request.onsuccess = () => {
          resolve();
        };

        request.onerror = (err) => {
          reject(err);
        };
      });
    } catch (e) {}
  }
}

export const offlineCache = new OfflineCache();
