---
name: no-mock
description: Enforces production fallback strategies, evidence-based verification, reuse-over-rebuild, atomic code organization, and user-facing UI copy — avoiding mock data, mock scope, placeholder UI, emoji-as-icons, exposed backend/infra details, and demo-only implementations. Defines a hard completion gate — a feature cannot be reported "done" unless it passes explicit checks.
---

# Core Principles

Every section below applies one or more of these three rules. They're stated once here; the rest of this file references them by name instead of re-explaining them.

1. **Verify, Don't Assume** — a claim that something works, is fixed, or is unnecessary must be backed by something actually executed, run, or checked in this session — not inferred from reading code, from one pass, or from what's fastest to conclude.
2. **Disclose, Don't Decide Silently** — any scope cut, workaround, blocker, assumption, or unverified piece must be stated to the developer in the response. Making that call and not mentioning it is the violation, not the decision itself.
3. **Root Cause, Not Symptom** — a fix addresses why something failed, not just that it stopped failing. A workaround is acceptable only if it's labeled as one, with the real fix named.

---

# Production Fallback Strategy

In a production environment, returning dummy or mock values masks real failures, exposes incorrect state to users, and obscures API errors. Instead of dummy fallbacks, production code should use:

1. **Explicit Error & Empty States**: Render actionable UI banners (`<ErrorAlert message="..." onRetry={...} />`) or empty state components (`<EmptyState />`) that allow users to retry.
2. **Skeleton Loading UI**: Display skeleton loaders or progress indicators while network requests are pending.
3. **Type-Safe Nullable / Empty Initialization**: Initialize data structures to empty values (`[]`, `null`) handled cleanly by the view layer.
4. **Telemetry & Error Logging**: Log network exceptions to monitoring platforms (e.g., Sentry) with structured metadata.
5. **Retry Policies**: Apply exponential backoff retries for transient API network failures.

Wire frontend components directly to real endpoints and typed API contracts (`fetch`, `axios`, `React Query`, `SWR`). No dummy records or fake arrays in production code paths.

---

# Reuse Over Rebuild

Before building infrastructure, UI, or a specialized capability from scratch, check whether an established template, component library, platform, or service already provides it — and default to that as the starting point.

- **Frontend**: start from a pre-built template/scaffold, not a blank file. Use enterprise component libraries (shadcn/ui, Radix, MUI, Ant Design, or the stack equivalent) for buttons, forms, modals, tables — theme them, don't reimplement them.
- **Backend/automation**: check for an existing automation platform (n8n, Zapier, Make, Temporal) before building a custom orchestration engine. Prefer managed services (auth, hosting, storage, email, background jobs) over self-hosted equivalents.
- **Specialized tasks**: for scraping, prefer an existing platform (Apify, ScraperAPI, Browserless) over hand-building a scraper and anti-bot handling. Same logic for OCR, payments, search, image/video processing, transcription — check for the established platform first.

**Custom build is justified only when**, per *Verify*: no suitable option was found after actually checking (not assumed absent); or an existing option has a named hard blocker (cost at scale, compliance, missing feature, latency, lock-in); or the developer explicitly asked for custom. Either way, name what was considered and state the choice — per *Disclose*.

This isn't license to bolt on a random tool without judgment, or to skip something the developer specifically wants owned in-house.

---

# Code Organization, Naming, and Refactoring

Dumping a feature into one long file is a demo-ism — it signals "get it working" over "build it to last."

**Atomic structure**
- Organize UI by composability: atoms (buttons, inputs, icons) → molecules (form fields, cards) → organisms (navbars, forms, tables) → templates/pages, in a matching folder structure.
- New UI needs are built by composing existing atoms/molecules first; a new one is created only when the existing set genuinely doesn't cover it.
- Backend gets the same discipline: routing/controllers, business logic, and data access stay in separate layers, not one file.

**Reusable Component Libraries**
- A component is authored once, in the shared component directory, and imported wherever needed — never re-authored inline inside a screen file.
- Check the shared library before writing anything new. If an equivalent exists, import and extend it via props rather than recreating a near-duplicate.
- Build components prop-driven and screen-agnostic from the start, so the same import works unmodified on the next screen, not just the first.
- Near-identical markup/logic appearing in more than one file is the signal to extract immediately — two copies are two places to drift out of sync.

**Routing**
- Routes are defined through the framework's actual router (routes config, file-based router, route tree) — never simulated with conditional rendering or local state standing in for navigation.
- Each screen is a distinct, addressable route, not a hidden view toggled by component state.
- Shared layouts, redirects, and auth guards use the router's own primitives, not hand-rolled equivalents.

**File naming**
- Short, specific, and following the stack's convention (`kebab-case` files, `PascalCase` components) — not sentence-length names, not generic dumping grounds (`utils.js`, `misc.ts`).
- If a file's name needs "and" to describe it, it's doing too much and should be split.

**Refactoring discipline**
- A file mixing data-fetching, business logic, and UI must be split by responsibility when touched, not left because it "works" — and per *Disclose*, name that a refactor happened alongside the feature work.
- Refactors preserve behavior; re-verify against the Completion Gate rather than assuming a reorganization is safe (*Verify*).

---

# The Completion Gate

A feature, page, or flow may not be reported as **done**, **complete**, **working**, or **ready** unless every applicable item below is true. If any item is false, the correct report is: "**Partial** — X is wired and verified; Y and Z are not," with what's missing and what verifying it would require. A partial result stated honestly is fine. A partial result reported as complete is the specific failure this skill exists to prevent.

**Scope**
- [ ] Every user-facing action (button, form, link) is wired to real logic — none inert, `console.log`-only, or `TODO`.
- [ ] Every production UI state is handled: loading, empty, error, partial/degraded, success — not just success.
- [ ] Every primary user journey has been traced start to finish, not just the happy branch.
- [ ] New code follows the codebase's atomic structure and naming convention — not a single file mixing concerns because that was faster.
- [ ] A needed component was imported from the shared library if it existed, or added to that library if it didn't — not duplicated as a one-off.
- [ ] Navigation goes through the framework's real router, not conditional rendering standing in for it.
- [ ] User-facing text/status passes the two-question test in the UI Copy Standard below — no vendor names, system-state language, or non-actionable status in shipped UI.

**Data**
- [ ] No hardcoded arrays, fake IDs, lorem ipsum, placeholder images, or invented numbers in the shipping path. If a real data source doesn't exist yet: explicit empty/error state plus a flagged blocker (*Disclose*), not synthetic data standing in for it.
- [ ] Data edge cases (empty, single item, max-length, null/undefined) were exercised, not assumed from the type signature (*Verify*).

**Verification** (*Verify*)
- [ ] The flow was actually run/clicked/called against real wiring this session — not just read and reasoned about.
- [ ] At least one non-happy-path case was executed, not imagined.
- [ ] If verification was blocked, the blocker is named and the feature reported unverified (*Disclose*) — never reported as working because the code "looks right."
- [ ] Any claim that a mock, demo, placeholder, or from-scratch build was "required" was checked against real evidence, not assumed as the fastest path to an answer.

**Production Readiness**
- [ ] Checked against scalability (concurrent/high-volume load, not one call), security (no weakened auth/validation, no exposed secrets), cost (no unbounded retries/polling), availability (no new single point of failure), and latency (not just fast on one local run).
- [ ] If built in response to an error, the fix addresses why it happened, not just that it stopped appearing (*Root Cause*).
- [ ] If the production-grade version is out of scope this session, that's disclosed as a blocker (*Disclose*) — not silently swapped for a narrower version reported as done.

**Disclosure** (*Disclose*)
- [ ] Any scope reduction or deferred edge case was made visible in the response — never decided and silently folded into a "complete" report.

# Reporting Format

Every completion report uses three explicit buckets — don't compress them into one summary line.

- **Verified**: ran/executed/inspected directly this session — list what.
- **Assumed**: reasoned from structure but not executed — list what and why.
- **Unverified/blocked**: not checked, and why.

If **Unverified/blocked** touches a primary user journey, the feature-level status is **Partial**, full stop, regardless of how much surrounding code is finished.

---

# Tests as Intent, Not Description

Tests must encode the requirement, not restate what the implementation currently does — a test derived from the requirement catches bugs; one derived from the code just re-describes the bug. Treat generated tests as a risk surface: review assertions as carefully as the implementation, since a wrong test that passes certifies broken behavior as correct. A passing test doesn't substitute for realistic conditions (*Verify*) — confirm it matches production shape (real data volume, concurrency, failure modes), not an idealized mock.

---

# Root Cause Over Shortcut

When an error or blocker arises mid-task, the default failure mode is fixing it just enough to get the run to succeed and moving on. This is prohibited (*Root Cause*).

**Named shortcut patterns to refuse:**
- Increasing a timeout/retry/limit to mask a slow query or unbounded operation, instead of fixing the inefficiency.
- Disabling or weakening auth, CORS, or validation to unblock a request, instead of configuring it correctly.
- Catching and silently swallowing an exception so the flow "completes."
- Hardcoding a value to route around a failing integration, instead of fixing the integration.
- Loosening a rate limit or concurrency guard because it caused a failure under load, instead of designing for the load.
- A fix that only works for a single instance/user/request and would race under real concurrent conditions.
- An in-memory or local-file workaround for state that needs to survive a restart or scale horizontally.

If the true fix is out of scope this session, name that as a blocker (*Disclose*) rather than substituting a workaround and reporting the error resolved. A necessary temporary workaround must be labeled as one, with the real fix named — never folded into a "fixed" report.

---

# Rejected Excuses

Under time or token pressure, these specific rationalizations are disallowed as a basis for marking something complete — each is a *Verify* or *Disclose* violation wearing different words:

- "This edge case is unlikely" / "I'll leave this for a follow-up" → gate it or disclose it as skipped; don't decide silently.
- "The code looks correct" / "One test passed" → that's Assumed, not Verified.
- "It works now, the error's gone" → confirm the root cause was addressed, not masked.
- "This is fine for now" / "just to get it running locally" → not a production-grade claim unless the developer said current scale is the bar; label it as local-only if it is.

---

# Iconography and Typography Standard

Emoji anywhere in UI chrome or frontend component code is a demo-ism — it renders inconsistently across platforms and signals prototype over shipped product.

- **App-generated AI content** (a chatbot's message text, LLM output rendered as content) is unrestricted — that's content, not UI, and this rule doesn't apply to it.
- **UI chrome the coding agent authors** — buttons, icons, nav, badges, logos, empty states, toasts, labels, placeholder/seed copy — must never use emoji, full stop.
- Use **Material Symbols/Material Icons (Google Fonts)** for all iconography, referenced by name/ligature or component — never an emoji character.
- Use **Google Fonts** for typography rather than system defaults or placeholders.
- Logos and brand marks are custom assets or proper icon-library glyphs, never emoji standing in for one.

---

# UI Copy Standard: Represent the User's Model, Not the System's

An interface represents the user's mental model of their task — what they're doing and what happened — not the engineer's model of how the system is built.

**The test for any UI text or status element:**
1. Would this make sense to someone with zero knowledge of how the app was built? If it names a technology, protocol, vendor, or internal system state, it fails.
2. Does this change what the user does next? If not, it belongs in a log, not a screen. If it does, state the action-relevant consequence, not the internal cause.

Anything failing either question gets rewritten or removed. This is the reasoning to apply generally, not a list to check against.

**In practice:**
- Labels name the user's action/object (`Send`, `Repeat`, `Devices`), not the mechanism.
- Status states the outcome and, if relevant, what to do — "You're offline — changes will sync when you're back" passes; "WebSocket disconnected, reconnecting..." fails.
- Errors say what happened to the user's action and what they can try — "Couldn't send your message. Try again." passes; "POST /api/messages returned 500" fails.
- Decorative technical indicators (connection dots, version badges, raw service-status widgets) fail question 2 outright and don't belong in shipped UI at all.

**Exception**: attribution contractually required by a vendor's terms of service (a mandated "Powered by X" badge) is a compliance obligation, not a design choice — apply it only where required.
