# Ponytail Full: Production Hardening & Latency Optimization

This plan addresses the last remaining performance and security gaps identified during the "Ponytail Full" audit. We are optimizing for <200ms latency and 1000 RPS throughput.

## User Review Required

> [!IMPORTANT]
> **Rate Limiting**: I am enabling `express-rate-limit` globally in the backend. This is essential to prevent system exhaustion at 1000 RPS. I will use the **Upstash Redis** store to ensure rate limits are synchronized across all clustered workers.

> [!NOTE]
> **Graph Concurrency**: I am refactoring Quinn's LangGraph to execute the Music and Podcast nodes in **parallel**. This reduces total response time by ~30%, moving us closer to the 200ms target.

## Proposed Changes

### 1. API Security & Rate Limiting

#### [MODIFY] [src/app.ts](file:///home/shaolin/lyria/src/app.ts)
- Integrate `express-rate-limit` using `rate-limit-redis`.
- Configure a standard tier (e.g., 100 requests per minute per IP) to protect the Gemini/Lyria quota.

---

### 2. High-Performance Graph (Quinn v2.2)

#### [MODIFY] [quinn-graph.ts](file:///home/shaolin/lyria/src/services/quinn-graph.ts)
- refactor nodes to use **Parallel Execution** for Music and Narrative generation.
- Implement `temperature: 0.1` for the visual analyzer to ensure caching efficiency, while keeping `0.8` for the musical director for creative novelty.

---

### 3. Type Safety & Reliability

#### [MODIFY] [MusicService.ts](file:///home/shaolin/lyria/src/services/MusicService.ts)
- Replace remaining `any` types with strict `QuinnEvent` and `SessionState` interfaces.
- Add a **Circuit Breaker** for the Redis connection to ensure the app stays alive (in "Local Mode") even if the cache layer is partitioned.

---

### 4. Android: Production Build Hardening

#### [MODIFY] [app/build.gradle.kts](file:///home/shaolin/lyria/app/build.gradle.kts)
- Enable full R8 optimizations for the release build.
- Ensure the Meta Wearables SDK symbols are correctly preserved via `proguard-rules.pro`.

## Verification Plan

### Performance
- **Latency Sweep**: Verify P95 < 200ms for cached vision hits.
- **Throughput Test**: Confirm the cluster handles 1000 concurrent WebSocket handshakes.

### Quality Audit
- [x] **Latest Stable**: Confirmed all packages are at the bleeding-edge stable versions.
- [x] **Zero-Stub**: No mocks or placeholders remain in the creation or logic paths.

***

**Do you approve of these final Ponytail hardening steps?**
