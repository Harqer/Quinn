---
title: AGENTS
source: local
author: AI Agent
published: false
created: 2026-07-16
description: Coding guidelines and architectural rules.
tags: [guidelines, architecture]
---

# JavaScript/TypeScript Coding Standards
*Personal project standard — last updated July 2026*

> Philosophy: consistency beats cleverness. These rules exist so you don't have to re-decide the same thing every time you open the editor. If a rule ever fights the goal of shipping working code, the goal wins — update the doc instead of arguing with yourself.

---

## 1. Tooling (set once, then forget)

| Tool | Purpose | Config |
|---|---|---|
| **TypeScript** | Type safety | `strict: true` in `tsconfig.json`, no exceptions |
| **ESLint** | Linting | `@typescript-eslint/recommended` + `eslint-plugin-import` |
| **Prettier** | Formatting | Defaults, 2-space indent, semicolons on, single quotes |
| **Vitest / Jest** | Testing | Pick one, don't mix |
| **Husky + lint-staged** | Pre-commit hooks | Run lint + format on staged files only |

**Rule:** Formatting is never a judgment call — Prettier decides, you don't argue with it. If Prettier and your instinct disagree, Prettier wins; change the config once if it's really wrong, don't hand-fix output.

---

## 2. TypeScript specifics

- **`strict: true`** always. No `any` unless it's genuinely unknown external data — and even then, prefer `unknown` + narrowing.
- **Types vs Interfaces:** use `interface` for object shapes that might be extended (props, entities); use `type` for unions, tuples, and utility compositions. Don't agonize past that.
- **No implicit `any`** — if TS can't infer it, annotate it.
- **Avoid `enum`** — prefer `as const` object maps or union string literals; they're more tree-shakeable and easier to serialize.
- **Null vs undefined:** pick `undefined` for "not set yet" and reserve `null` for "explicitly empty" (e.g., API returned no value). Don't use both interchangeably.
- **Non-null assertion (`!`)** is a last resort, not a shortcut — if you need it more than rarely, the types are wrong somewhere upstream.

---

## 3. Naming conventions

| What | Convention | Example |
|---|---|---|
| Variables, functions | `camelCase` | `getUserById` |
| Classes, types, interfaces | `PascalCase` | `UserProfile` |
| Constants (true constants) | `UPPER_SNAKE_CASE` | `MAX_RETRIES` |
| Files (non-component) | `kebab-case.ts` | `user-service.ts` |
| React components | `PascalCase.tsx` | `UserCard.tsx` |
| Booleans | prefix `is/has/should/can` | `isLoading`, `hasError` |
| Private class members | `#field` (real private) | `#cache` |

**Rule of thumb:** name things for what they *are*, not how they're implemented. `activeUsers` not `filteredArr2`.

---

## 4. File & folder structure

```
src/
  features/            # group by feature, not by file type
    auth/
      auth.service.ts
      auth.types.ts
      login-form.tsx
      auth.test.ts
    users/
  shared/
    components/
    hooks/
    utils/
  lib/                  # third-party wrappers/config (e.g. api client)
```

- **Colocate tests** next to the file they test (`thing.ts` + `thing.test.ts`), not in a mirrored `__tests__` tree.
- **One default export per file max**, and prefer named exports overall — easier to refactor and grep.
- **Barrel files (`index.ts`)** only at feature boundaries, not everywhere — they slow down builds and obscure import paths if overused.

---

## 5. Function & code style

- **Functions do one thing.** If you need "and" to describe it, split it.
- **Prefer pure functions** where possible — same input, same output, no hidden state mutation.
- **Early returns over nested conditionals:**
  ```ts
  // Good
  function process(user?: User) {
    if (!user) return null;
    if (!user.isActive) return null;
    return doWork(user);
  }
  ```
- **Arrow functions** for callbacks/inline logic; **named function declarations** for top-level functions (better stack traces, hoisting is fine here).
- **No magic numbers/strings** — extract to a named constant if it means something.
- **Max function length ~40 lines** as a smell-detector, not a hard rule — if you blow past it, ask whether it should be two functions.

---

## 6. Async & error handling

- **Always `async/await`** over raw `.then()` chains.
- **Never swallow errors silently.** A bare `catch {}` is a bug waiting to happen — at minimum log it, ideally handle or rethrow with context.
- **Custom error classes** for domain errors so callers can `instanceof` check:
  ```ts
  class NotFoundError extends Error {
    constructor(resource: string) {
      super(`${resource} not found`);
      this.name = 'NotFoundError';
    }
  }
  ```
- **Wrap external calls** (fetch, DB, third-party SDKs) in a try/catch at the boundary — don't let raw network errors leak into UI code.

---

## 7. Imports

Order, top to bottom, blank line between groups:
1. Node/external packages (`react`, `zod`, etc.)
2. Internal absolute imports (`@/features/...`)
3. Relative imports (`./`, `../`)
4. Types (can mix in or use `import type` explicitly — be consistent)

Use `import type { Foo } from './foo'` for type-only imports so they're erased at build time.

---

## 8. Comments & documentation

- **Comment the *why*, not the *what*.** Code already says what it does; comments explain the non-obvious reasoning, trade-off, or gotcha.
- **JSDoc on exported functions** that aren't self-explanatory from name + types alone — skip it for trivial getters.
- **No commented-out code** left in commits — delete it, git remembers.
- **TODO comments** must include context: `// TODO(you): revisit once API v2 ships pagination`

---

## 9. Git & commits

- **Conventional commits:** `feat:`, `fix:`, `refactor:`, `chore:`, `docs:`, `test:`
- **One logical change per commit.** Small, revertible, readable in `git log`.
- **Present tense, imperative mood:** `fix login redirect bug`, not `fixed` or `fixes`.
- **Never commit** `.env`, secrets, or `node_modules` — `.gitignore` set up front.

---

# Production Readiness Checklist
*Android • Firebase • Gemini Lyria • Meta Wearables (camera + real-time voice) • Play Store*

> This assumes the app is built and working. The goal here is finding what breaks under real users, real cost, and real Play Store review — not code style. Go section by section; each has a "why this matters for your specific app" note because your stack (continuous camera + mic + AI generation) has failure modes a typical CRUD app doesn't.

---

## 1. Security

**Why it matters more for you:** you're streaming camera frames and live audio off someone's face. That's a materially higher trust bar than most apps.

- [ ] **No API keys in the client.** Gemini Lyria key, any Meta/third-party keys — none of them should be bundled in the APK. Route all Gemini calls through a Cloud Function / Cloud Run proxy that holds the key server-side.
- [ ] **Firebase App Check enabled** — prevents unauthorized clients (scraped API endpoints, bots) from hitting your Cloud Functions or Firestore directly.
- [ ] **Firestore/RTDB security rules reviewed line-by-line** — default-deny, then explicit allow per collection. Test rules with the Firebase emulator, not just "it works from my app."
- [ ] **Auth tokens refreshed properly** — verify `onIdTokenChanged` (not just `onAuthStateChanged`) so expired tokens don't silently fail mid-session during a long voice call.
- [ ] **Camera/mic data in transit is encrypted** (WSS/TLS for any WebRTC signaling or socket connection — never plain `ws://`).
- [ ] **No raw camera frames or audio persisted to Firestore/Storage unless required** — if you only need frames transiently for the "vibe" inference, don't store them. If you do store them (e.g., for regenerating a track later), they need explicit retention limits and user-facing deletion controls.
- [ ] **PII minimization** — if the camera can capture bystanders, you need a stated policy (Play Store will ask about this — see §9).
- [ ] **Rate limit Cloud Function endpoints** per user/device to prevent abuse driving up your Gemini bill (see §6).
- [ ] **Meta Wearables permission flow uses "Allow once" vs "Allow always" correctly** — don't request persistent access if the feature only needs it during an active session.
- [ ] **Secrets in Cloud Functions use Firebase Secret Manager**, not `.env` files committed anywhere or plaintext `functions.config()`.

---

## 2. Database schema (Firestore/RTDB)

**Why it matters more for you:** real-time voice + live camera inference generates a *lot* of write/read volume fast. Bad schema = bad bill and bad latency simultaneously.

- [ ] **No unbounded arrays in documents** (e.g., a growing list of every "vibe" or session event inside a user doc) — Firestore docs cap at 1MB and large docs get slower to read/write. Use a subcollection instead.
- [ ] **Avoid hot documents** — if many clients write to the same doc concurrently (e.g., a shared session/room doc updated on every frame), you'll hit contention. Shard or move high-frequency fields to a subcollection/RTDB path instead.
- [ ] **Composite indexes created for every query you actually run** — check the Firebase console for "missing index" errors in production logs, not just local dev.
- [ ] **Real-time listeners are scoped tightly** — don't listen to a whole collection when you need one doc; every listener update is a live cost and bandwidth line item.
- [ ] **Session/vibe history modeled as its own collection** with a `userId` + `createdAt` index, not nested inside the user document.
- [ ] **RTDB vs Firestore choice matches the workload** — RTDB is typically better for very high-frequency ephemeral data (live voice state, presence); Firestore better for structured, queryable history (past generated tracks, user library). If you're using one for both, double check it's the right fit.
- [ ] **TTL / cleanup policy** for ephemeral session data — Firestore TTL policies or a scheduled Cloud Function to purge old session/camera-derived data.
- [ ] **Backups configured** (scheduled Firestore export to Cloud Storage) in case of accidental bulk delete or bad migration.

---

## 3. Real-time voice & camera streaming

**Why this is its own section:** this is the part most likely to fall over under real network conditions (not your wifi).

- [ ] **Reconnect logic tested** — what happens when a user walks out of wifi range mid-voice-session? Does the session resume, or silently die?
- [ ] **Jitter buffer / backpressure handling** for audio — verify behavior on 3G/weak LTE, not just good wifi.
- [ ] **Meta Wearables session lifecycle handled for all states** — glasses folded, glasses powered off mid-stream, Bluetooth disconnect. Don't hardcode an assumed reason for a `STOPPED` state; check the actual state transition.
- [ ] **Battery impact tested** — continuous camera streaming + real-time voice + AI generation is heavy; measure actual battery drain per session length on a real Android device, not emulator.
- [ ] **Background/foreground service behavior** — Android increasingly restricts background mic/camera access. Verify your foreground service notification is correctly declared if audio/camera continues when the app isn't focused (required for Play Store compliance, not optional).
- [ ] **Graceful degradation** if Gemini Lyria is slow/unavailable — does the user get a "generating..." state or does the app appear frozen?

---

## 4. Caching

**Why it matters more for you:** Gemini Lyria generation is presumably not free or instant — regenerating the same "vibe" twice is wasted latency and wasted money.

- [ ] **Cache generated tracks by input signature** (e.g., hash of the camera-derived "vibe" descriptor + params) so an identical input doesn't re-trigger generation.
- [ ] **CDN/Firebase Hosting cache headers set correctly** for any static assets (album art, UI assets) — long cache + cache-busting filenames, not no-cache everywhere.
- [ ] **Client-side memory cache for recently generated tracks** during a session, so switching between "vibes" the user already generated doesn't re-hit the network.
- [ ] **Cache invalidation policy is explicit** — if a user regenerates or edits a vibe, make sure stale cached audio isn't served back to them.
- [ ] **Firestore read caching** — for data unlikely to change often (user preferences, static config), use `get()` with cache-first settings rather than always paying for a live listener.

---

## 5. Batching

**Why it matters more for you:** camera-derived inference and voice data naturally arrive as a stream — sending every frame/chunk as its own API call is both slow and expensive.

- [ ] **Camera frames batched/throttled before inference**, not sent at full framerate — decide the minimum sampling rate that still produces good "vibes" (e.g., 1 frame every N seconds) and cap it.
- [ ] **Firestore writes batched** using `WriteBatch` for any multi-document update (e.g., logging a session + updating usage counters) instead of separate calls.
- [ ] **Analytics/usage events batched and flushed periodically**, not fired individually per frame or per voice chunk.
- [ ] **Gemini API calls batched where the API supports it**, and otherwise debounced so rapid-fire camera changes don't trigger a generation call per frame.

---

## 6. Cost effectiveness

**Why it matters more for you:** this is the one that quietly kills solo/indie AI apps — a viral moment with unthrottled Gemini + Firebase calls can produce a bill nobody planned for.

- [ ] **Per-user and per-day quota/rate limits** on Gemini Lyria generation calls — enforced server-side (Cloud Function), not just client-side (which is trivially bypassed).
- [ ] **Budget alerts set in Google Cloud/Firebase console** at multiple thresholds (e.g., 50%, 90%, 100% of expected monthly spend) — don't rely on discovering it via the invoice.
- [ ] **Firestore read/write cost modeled** — estimate cost per active session (listeners + writes) × expected DAU, not just per-document cost.
- [ ] **Cloud Functions cold start / invocation cost checked** — if a function fires per camera frame, that's a very different cost profile than per-session.
- [ ] **Egress bandwidth accounted for** — audio streaming both directions adds up; check Firebase/GCP egress pricing against expected session length × DAU.
- [ ] **Free tier vs paid tier usage tracked separately** if you're using Firebase Spark plan features anywhere — know your actual headroom before launch, not after.
- [ ] **Fallback/degraded mode defined** for when quotas are hit (e.g., "generation limit reached today" rather than the app silently failing or the bill silently climbing).

---

## 7. Observability & monitoring

- [ ] **Crashlytics (or equivalent) integrated** and verified to actually receive test crashes before launch.
- [ ] **Structured logging** on Cloud Functions (not just `console.log`) so you can query by session ID / user ID when debugging a specific bad session.
- [ ] **Alerting on Gemini API error rate** — if the third-party API degrades, you want to know before your users start complaining.
- [ ] **Alerting on cost anomalies** (see §6) tied to actual notification (email/Slack), not just a dashboard nobody checks.
- [ ] **Session success rate tracked** (voice sessions that complete vs. drop) as a real product metric, not just crash-free rate.

---

## 8. Security/permissions review specific to Meta Wearables

- [ ] **Production registration completed** with the Meta AI app (not just Developer Mode) — confirm `APPLICATION_ID` is set correctly for release builds, or your release APK will fail to connect to glasses for real users.
- [ ] **Permission rationale strings are clear and honest** — Android will show your camera/mic permission prompt; vague rationale increases both user distrust and Play Store review friction.
- [ ] **Tested with Mock Device Kit AND real hardware** before submission — simulator-only testing misses real Bluetooth/connection edge cases.

---

## 9. Play Store submission readiness

**This is the section most likely to get you rejected or delayed if skipped** — camera + microphone + AI-generated content together put you in a higher-scrutiny review bucket.

- [ ] **Privacy Policy published and linked** — mandatory, and must specifically address camera and microphone data use given your feature set (not a generic boilerplate policy).
- [ ] **Data Safety form completed accurately** in Play Console — declare camera, microphone, and any data shared with third parties (Google/Gemini). Mismatches between declared and actual behavior are a common rejection reason.
- [ ] **Sensitive permissions justified** in Play Console's permissions declaration form — camera + microphone + background service all require explicit justification text.
- [ ] **Target API level meets Play Store's current minimum** (check Play Console for the current requirement — this changes yearly, verify at submission time, not from memory).
- [ ] **Content rating questionnaire completed** — AI-generated audio content may need review for whether generated output could include unexpected content; consider if any content moderation is needed on generated output.
- [ ] **Bystander privacy addressed** — since the camera may capture people other than the user, consider whether you need on-device processing (frames never leave device) vs. cloud processing, and disclose accordingly. This is both a legal and review-risk issue.
- [ ] **Foreground service declaration matches actual behavior** if camera/mic continue when app is backgrounded — Play Store checks this against your manifest and will reject mismatches.
- [ ] **Tested on a range of real Android devices/OS versions**, not just your dev device — Bluetooth/glasses pairing behavior varies meaningfully across OEMs.
- [ ] **Closed testing track run first** (internal → closed → open) rather than going straight to production, especially given hardware-dependent (glasses) functionality.
- [ ] **Crash-free rate and ANR rate meet Play Console's health thresholds** before requesting full rollout — Play Store increasingly gates visibility/rollout speed on this.
- [ ] **Staged rollout percentage set** (e.g., 5% → 20% → 50% → 100%) rather than 100% on day one, so a bad build doesn't hit your whole user base at once.

---

## 10. Final go/no-go checklist

Run through this the day before submission:

- [ ] All of §1 (Security) checked
- [ ] Cost alerts are live and tested (§6) — send yourself a test alert
- [ ] Privacy Policy + Data Safety form are consistent with each other and with actual app behavior (§9)
- [ ] At least one full real-device test: fresh install → glasses pairing → live voice session → music generation → app backgrounded mid-session → resumed
- [ ] Staged rollout configured, not 100% release
- [ ] Rollback plan exists (previous APK version ready to re-promote if the new release regresses)
- [ ] Someone other than you has used the app once, cold, with no guidance

---

# Meta Wearables SDK Architecture Rules
Build exactly one root view per `sendContent` call: use a root `flexBox { ... }` for UI, or a root `video(player = player)` for video. Do not put `video(...)` inside a `flexBox`. Button and clickable `flexBox` callbacks are routed back to the phone app; keep callbacks fast and delegate to app state or ViewModel methods. Use `IconName` enum values such as `IconName.GEAR`, not raw strings.

For URL video, create `VideoPlayer(source = VideoSource.Url(...), codec = VideoCodec.MP4)`, send it with `display.sendContent { video(player = player) }`, and call `player.play()` after send success. Collect `player.state` and `player.error`; on `VideoPlayerState.ENDED`, cancel the video observer and send the next display screen. On cleanup, cancel state/error collection jobs, close or replace active video players, call `session.removeDisplay()`, then stop the session.
