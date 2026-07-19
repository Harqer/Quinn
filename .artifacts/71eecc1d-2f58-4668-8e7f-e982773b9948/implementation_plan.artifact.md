# implementation_plan.md: Gemini Streaming & RTDB Integration

This plan implements real-time text streaming using Gemini SDK, leverages Google Context Caching for massive developer instructions, and integrates Firebase Realtime Database for state synchronization across client and backend.

## User Review Required

> [!IMPORTANT]
> **Realtime Database**: I will add RTDB to the `firebase-admin` initialization. Please ensure that the `firebase-applet-config.json` includes the `databaseURL`.
> **Context Caching**: I will implement a caching service for long instructions. This requires the `GEMINI_API_KEY` to have sufficient permissions for the `cachedContents` API.

## Proposed Changes

### Backend: Firebase Integration

#### [MODIFY] [src/config/firebase.ts](file:///home/shaolin/lyria/src/config/firebase.ts)
- Import `getDatabase` from `firebase-admin/database`.
- Add `databaseURL` to `initializeApp` (derived from `firebaseConfig`).
- Export `rtdb`.

### Backend: Gemini Optimization

#### [MODIFY] [src/services/ai.ts](file:///home/shaolin/lyria/src/services/ai.ts)
- Implement `getContextCacheManager()` to interact with the Gemini caching API.
- Add logic to create/retrieve caches for massive developer instructions.

#### [MODIFY] [src/services/quinn-graph.ts](file:///home/shaolin/lyria/src/services/quinn-graph.ts)
- Refactor nodes to support streaming where applicable.
- Pass `cachedContent` ID to the `ChatGoogleGenerativeAI` instances if available.

### Backend: Service Layer (Streaming & RTDB Sync)

#### [MODIFY] [src/services/MusicService.ts](file:///home/shaolin/lyria/src/services/MusicService.ts)
- Switch from `quinnGraph.invoke()` to `quinnGraph.stream()`.
- Pipe text chunks to the WebSocket in real-time as `quinn_chunk` events.
- Update `RTDB` at `/sessions/{sessionId}/state` on every important state change to keep clients in sync with minimal overhead.

## Verification Plan

### Automated Tests
- Run `pnpm test` to ensure basic API schemas still pass.
- Verify WebSocket connection handles `quinn_chunk` events.

### Manual Verification
- Monitor the Firebase Console (Realtime Database) to see state updates in real-time during a session.
- Check backend logs for "Context Cache Hit" to verify caching efficiency.
- Test the frontend "Studio" to ensure text appears chunk-by-chunk rather than all at once.
