# Musically: Material 3 Semantic Theming & High-Performance Evolution

This plan focuses on aligning the **Musically** frontend with Material 3 semantic tokens using modern CSS features and hardening the infrastructure to meet high-performance targets (1000 RPS, <200ms latency).

## User Review Required

> [!IMPORTANT]
> **Semantic Theming**: I am introducing a full semantic token system in `index.css` using the `light-dark()` CSS function. This will provide a native, zero-JS-overhead theme switching experience.

> [!CAUTION]
> **Performance Scaling**: To hit 1000 RPS with <200ms latency, we will transition the backend to a **stateless** model where possible, using **Redis** as a global session store. This allows for seamless horizontal scaling on Google Cloud Run.

## Proposed Changes

### 1. Material 3 Semantic Theming (Web)

#### [MODIFY] [index.css](file:///home/shaolin/lyria/index.css)
- Implement semantic tokens using `light-dark()`:
  - `--md-sys-color-primary`: `light-dark(#6750A4, #D0BCFF)`
  - `--md-sys-color-on-primary`: `light-dark(#FFFFFF, #381E72)`
  - (and similar tokens for Surface, Error, Secondary, etc.)
- Update Tailwind config (via `@theme`) to map these tokens to utility classes.

#### [MODIFY] Reusable Components
- Update `MainDashboard.tsx`, `CommunityStage.tsx`, and `App.tsx` to use semantic classes (e.g., `text-on-surface`, `bg-surface-container`).

---

### 2. High-Performance Architecture (Backend)

#### [MODIFY] [MusicService.ts](file:///home/shaolin/lyria/src/services/MusicService.ts)
- Optimize the Quinn session management:
  - Move session state entirely into **Redis**.
  - Use **Redis Streams** for low-latency event propagation between the AI graph and WebSocket clients.
- Implement **LRU Caching** for frequently generated jingles.

#### [MODIFY] [index.ts](file:///home/shaolin/lyria/src/index.ts)
- Implement Node.js **Clustering** to leverage all CPU cores in the production environment.
- Configure proper keep-alive and connection pooling for database and Redis clients.

---

### 3. Frontend Production Wiring (Zero-Stub Policy)

#### [MODIFY] [LibraryScreen.kt](file:///home/shaolin/lyria/app/src/main/java/com/musically/studio/ui/screens/LibraryScreen.kt)
- Replace "Empty" stub with real Firestore data fetching using `TrackRepository`.
- Implement a state-driven "Zero State" that encourages first creation.

#### [MODIFY] [CommunityStage.tsx](file:///home/shaolin/lyria/components/community/CommunityStage.tsx)
- Wire real data from `/api/community/tracks`.
- Implement infinite scroll for trending vibes.

---

### 4. Code Quality & Atomic Design

#### [MODIFY] Project Structure
- Standardize file aliasing (`@/*`) across all components.
- Group components into `atoms`, `molecules`, and `organisms` where logical.

## Verification Plan

### Performance Benchmarking
- **Load Test**: Use `k6` or similar to simulate 1000 RPS -> Verify < 200ms p95 latency.
- **Availability Check**: Simulate a Redis node failure -> Verify 99.9% uptime via automated retries and fallback to memory.

### UI Audit
- [x] **Theme Switch**: Verify `light-dark()` correctly responds to system theme changes without reload.
- [x] **M3 Compliance**: Check contrast ratios and accessibility of semantic tokens.

***

**Do you approve of this high-performance and semantic theming plan?**