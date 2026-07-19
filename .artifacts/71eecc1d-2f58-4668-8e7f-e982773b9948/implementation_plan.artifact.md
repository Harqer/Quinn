# Production Audit & Feature Realization Plan

This plan addresses the gaps identified in the "Saving, Bookmarking, and Reporting" systems. We are removing all remaining placeholders and fully wiring the frontend actions to our real-time production backend and database.

## User Review Required

> [!IMPORTANT]
> **Reporting System**: I am introducing a new `ReportRepository` and a corresponding `POST /api/reports` endpoint. This allows users to report inappropriate content or system bugs directly from the "Studio" or "Community" screens.

> [!NOTE]
> **Library Categorization**: I will update the `Track` schema to include a `type` field (`music` | `podcast`). This ensures that saved items appear in the correct section of the user's Spotify library and our local Firestore library.

## Proposed Changes

### 1. Database & Repository Hardening

#### [MODIFY] [TrackRepository.ts](file:///home/shaolin/lyria/src/repositories/TrackRepository.ts)
- Add `type: 'music' | 'podcast'` to the `Track` interface.
- Add `bookmarkTrack(uid: string, trackId: string)` method to handle saving community vibes to a user's personal collection.

#### [NEW] [ReportRepository.ts](file:///home/shaolin/lyria/src/repositories/ReportRepository.ts)
- Implement `saveReport(report: Report)` to store user-submitted reports for moderation.

---

### 2. Backend API Realization

#### [MODIFY] [spotify.ts](file:///home/shaolin/lyria/src/routes/spotify.ts)
- Update `savePodcastToPlaylist` logic to include better metadata so Spotify recognizes them as "Talk" content where possible.

#### [NEW] [src/routes/reports.ts](file:///home/shaolin/lyria/src/routes/reports.ts)
- Create endpoints for submitting content and user reports.

---

### 3. Frontend Component Finalization

#### [MODIFY] [CommunityStage.tsx](file:///home/shaolin/lyria/src/web/features/community/CommunityStage.tsx)
- Add "Bookmark" and "Report" buttons to `TrackCard`.
- Replace the Unsplash placeholder with actual image URLs from the database.

#### [MODIFY] [HomeScreen.kt](file:///home/shaolin/lyria/app/src/main/java/com/musically/studio/ui/screens/HomeScreen.kt)
- Add a "Save to Library" button to Quinn's chat bubbles in the Studio history.
- Wire the button to `MainViewModel.saveTrackToLibrary()`.

---

### 4. Code Cleanup (No-Mock Policy)

#### [DELETE] Remaining Stubs
- Remove the `// In production` comments and replace with real `AIApiService` or `WebSocket` calls.
- Fix the `spotify:track:placeholder` URIs in `PodcastScreen.kt` and `PodcastView.tsx`.

## Verification Plan

### Database Integrity
- **Real-time Flow**: Create a track -> Bookmark it -> Verify it appears in the `user_bookmarks` collection with a reference to the original track.
- **Report Validation**: Submit a report -> Verify it appears in `system_reports` with the correct timestamp and user context.

### Performance & Scalability
- [x] **1000 RPS**: Ensure repositories use efficient indexing for `where` and `orderBy` queries.
- [x] **Latancy**: Monitor the 200ms P95 target during saving/bookmarking operations.

***

**Do you approve of this plan to fully realize the Saving, Bookmarking, and Reporting features?**
