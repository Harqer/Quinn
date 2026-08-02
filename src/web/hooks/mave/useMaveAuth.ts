import { useCallback } from 'react';
import { getAuth } from 'firebase/auth';

export function useMaveAuth() {
  const getBaseUrl = useCallback(() => {
    return (import.meta.env.VITE_API_URL as string) || 'http://127.0.0.1:8081';
  }, []);

  const getAuthToken = useCallback(async (): Promise<string | undefined> => {
    try {
      const auth = getAuth();
      return await auth.currentUser?.getIdToken();
    } catch {
      return undefined;
    }
  }, []);

  return { getBaseUrl, getAuthToken };
}
