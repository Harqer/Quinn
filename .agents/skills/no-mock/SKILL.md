---
name: no-mock
description: Enforces production fallback strategies, evidence-based verification, reuse-over-rebuild, atomic code organization, and user-facing UI copy — avoiding mock data, mock scope, placeholder UI, emoji-as-icons, exposed backend/infra details, and demo-only implementations. Defines a hard completion gate — a feature cannot be reported "done" unless it passes explicit checks.
---

# Production Fallback Strategy

In a production environment, returning dummy or mock values masks real failures, exposes incorrect state to users, and obscures API errors. Instead of dummy fallbacks, production code should use:

1. **Explicit Error & Empty States**: Render actionable UI banners (`<ErrorAlert message="..." onRetry={...} />`) or empty state components (`<EmptyState />`) that allow users to retry.
2. **Skeleton Loading UI**: Display skeleton loaders or progress indicators while network requests are pending.
3. **Type-Safe Nullable / Empty Initialization**: Initialize data structures to empty values (`[]`, `null`) handled cleanly by the view layer.
4. **Telemetry & Error Logging**: Log network exceptions to monitoring platforms (e.g., Sentry) with structured metadata.
5. **Retry Policies**: Apply exponential backoff retries for transient API network failures.

## Enforcement Guidelines

* **No Mock Implementations**: Wire frontend components directly to real endpoints, backend services, or strongly-typed API contracts using HTTP clients (`fetch`, `axios`, `React Query`, `SWR`).
* **Production Fallbacks**: Prohibits dummy records and fake arrays in production code paths in favor of empty states, skeleton screens, telemetry logging, and retry logic.

---

# Reuse Over Rebuild

Before building infrastructure, UI, or a specialized capability from scratch, the agent must check whether an established template, component library, platform, or service already provides it — and default to using that as the starting point, rather than hand-building the foundation.

**Frontend**
- Start from a pre-built template or scaffold (e.g. a proper starter kit, an established admin/dashboard template, a design-system starter) instead of coding layout, navigation, and page structure from a blank file.
- Use enterprise component libraries for buttons, forms, modals, tables, nav, etc. (e.g. shadcn/ui, Radix, MUI, Ant Design, or the equivalent for the stack in use) instead of hand-rolling each component. Drop in the library's component and theme it, don't reimplement it.
- Custom-coding a component from scratch is justified only when the component library genuinely doesn't offer it or the design requirement can't be met by theming/extending an existing one — and that gap should be named, not assumed.

**Backend / automation**
- For workflow/automation needs (scheduled jobs, integrations, notifications, data pipelines), check for an existing automation platform (e.g. n8n, Zapier, Make, Temporal, or a managed queue/cron service) before building a custom orchestration/automation engine.
- For infrastructure concerns (auth, hosting, storage, email delivery, background jobs), prefer a managed platform/service over standing up and maintaining the equivalent yourself, unless there's a stated reason it won't work here.

**Specialized tasks**
- For scraping, prefer an existing scraping platform (e.g. Apify, ScraperAPI, Browserless) over hand-building a scraper, browser automation, and anti-bot handling from scratch.
- The same logic applies to other specialized problem spaces with mature existing platforms: OCR, payments, search/indexing, image/video processing, transcription, etc. — check for the established platform first.

**Required before building custom, in any of the above:**
- [ ] Name the template/component library/platform actually considered for this need — don't silently assume none exists.
- [ ] If a free tier, trial, or generous usage-based plan covers today's requirement, prefer it over a custom build, even if a paid step-up exists later.
- [ ] State the choice made (reuse vs. custom) so it's visible to the developer, not buried in the implementation.

**Custom build is justified only when:**
- No suitable template/library/platform was found after actually checking (not assumed absent).
- An existing option has a stated hard blocker for this use case — cost at the required scale, a compliance/data-residency requirement, a missing feature, unacceptable latency, or vendor lock-in — and that blocker is named explicitly.
- The developer has explicitly asked for a custom/from-scratch implementation.

**What this doesn't mean:** this isn't license to bolt on a random third-party tool without judgment, or to skip something the developer specifically wants owned in-house. The default should be "check whether this is a solved problem with an existing template/platform" before defaulting to building the foundation by hand — and either way, that decision is disclosed, not silently made.

---

# Code Organization, Naming, and Refactoring

Dumping a feature into one long file is a demo-ism, not a production pattern — it signals "get it working" over "build it to last." Structure and naming are not cosmetic; they're what makes a codebase maintainable by someone other than the session that wrote it.

**Atomic structure**
- Organize UI code by composability, not by feature dumping: atoms (buttons, inputs, icons, labels) → molecules (form fields, cards, list items) → organisms (navbars, forms, data tables) → templates/pages that compose them. Use the folder structure that reflects this (e.g. `components/atoms`, `components/molecules`, `components/organisms`, or the equivalent convention for the stack in use).
- A new UI need is built by composing existing atoms/molecules first; a new atom/molecule is created only when the existing set genuinely doesn't cover it — not duplicated ad hoc inside a page file.
- Backend code gets the equivalent discipline: separate routing/controllers, business logic/services, and data access layers rather than one file doing all three.

**File naming**
- Names are short, specific, and follow the stack's established convention (e.g. `kebab-case` for files, `PascalCase` for components) — not long descriptive sentences crammed into a filename, and not generic dumping-ground names (`utils.js`, `helpers.js`, `misc.ts`, `index.ts` holding unrelated logic).
- A file name should say what the single thing in it does. If the name needs "and" to describe it, the file is doing too much and should be split.

**No monolithic single-file dumps**
- A file or component that mixes concerns — data fetching, business logic, and UI rendering all in one place — must be split by responsibility, not left as one long file because it "works."
- Signs a file needs splitting: it's grown long enough that scrolling replaces comprehension, it handles more than one responsibility, or a piece of it is copy-pasted (even partially) elsewhere in the codebase instead of imported.
- When a change touches a file already showing these signs, refactor it as part of the change rather than adding more to an already bloated file — and disclose that a refactor was done alongside the feature work, so it's visible rather than buried in an unrelated diff.

**Refactoring discipline**
- Refactors preserve behavior; a refactor that changes behavior is not a refactor, it's an undisclosed feature change riding along. Verify the refactored code still passes the same checks it did before (see Completion Gate) rather than assuming a reorganization is automatically safe.
- Duplicated logic or markup is extracted into a shared component, hook, or utility rather than copy-pasted a second time — but the extraction should be named/visible, not a silent structural change bundled with unrelated work.

---

# The Completion Gate

A feature, page, or flow may not be reported as **done**, **complete**, **working**, or **ready** unless every applicable item below is true. If any item is false, the correct report is: "**Partial** — X is wired and verified; Y and Z are not," followed by exactly what is missing and what verifying it would require. A partial result stated honestly is acceptable. A partial result reported as complete is the specific failure this skill exists to prevent.

**Scope**
- [ ] Every user-facing action on the surface (button, form, menu item, link) is wired to real logic — none are inert, `console.log`-only, `TODO`, or silently no-op.
- [ ] Every state the UI can be in in production is handled in code: loading, empty, error, partial/degraded, and success — not just success.
- [ ] Every primary user journey touching this feature has been traced start to finish, not just the primary/happy branch.
- [ ] New code follows the codebase's atomic structure and naming convention, and isn't a single file mixing data-fetching, logic, and UI just because that was faster to write.
- [ ] Any user-facing text or status element passes the two-question test (would this make sense with zero knowledge of the stack, and does it change what the user does next) — not a vendor name, system-state description, or non-actionable status leaking into shipped UI.

**Data**
- [ ] No hardcoded arrays, fake IDs, lorem ipsum, placeholder avatars/images, or invented numbers remain in the code path that will ship. If a real data source doesn't exist yet, the correct output is an explicit empty/error state and a flagged blocker — not synthetic data standing in for it.
- [ ] Data shapes and edge cases (empty list, single item, max-length input, null/undefined field) have been exercised, not assumed from the type signature.

**Verification**
- [ ] The flow was actually executed — run, clicked through, or called — against real wiring in this session. Reading the code and reasoning that it should work does not satisfy this.
- [ ] At least one non-happy-path case (bad input, failed request, empty result, concurrent/duplicate action) was executed, not just imagined.
- [ ] If verification was blocked (no credentials, no environment, no access to a real API), that blocker is named explicitly, and the feature is reported as unverified — never reported as working because the code "looks right."
- [ ] Any claim that something "requires" a mock, a demo, a placeholder, or a from-scratch build was checked against actual evidence (a real constraint that was verified) — not assumed as the default because it's the fastest path to an answer.

**Production Readiness (applies to every feature, whether or not an error occurred)**
- [ ] The implementation was checked against the production dimensions that matter for this system, not just checked for one happy request: scalability (works under concurrent/high-volume load, not just a single call), security (no weakened auth, exposed secrets, disabled validation, or opened attack surface), cost (no unbounded retries, polling, or resource use), availability (no single point of failure), and latency (no design choice that only performs acceptably on one local run).
- [ ] If a fix or feature was built in response to an error, the fix addresses why the error occurred, not just that it stopped appearing. "It runs now" is not the same as "it's fixed."
- [ ] If the true production-grade version of something is out of scope for this session (requires infra change, access the agent doesn't have, a decision only the developer can make), that is disclosed as a blocker — the agent does not substitute a narrower/local version and report it as done.

**Disclosure**
- [ ] Any scope reduction, deferred edge case, or "I'll handle this later" decision was made visible to the user in the response — never decided and silently absorbed into a "complete" report.

# Reporting Format

Every completion report uses three explicit buckets. Do not compress them into one summary line.

- **Verified**: ran, executed, or inspected output directly in this session — list what.
- **Assumed**: reasoned from code/type structure but not executed — list what and why it's a reasonable assumption.
- **Unverified / blocked**: not checked, and why (missing env, missing credentials, no test data, out of scope for this session).

If **Unverified/blocked** contains anything on a primary user journey, the feature-level status is **Partial**, full stop — regardless of how much of the surrounding code is finished.

# Tests as Intent, Not Description

1. Write or require tests that encode the requirement, not tests that restate what the implementation currently does. A test derived from the requirement catches bugs; a test derived from the code it's testing just re-describes the bug.
2. Treat generated tests as a risk surface: a wrong test that passes is worse than no test, because it certifies broken behavior as correct. Review test assertions with the same scrutiny as the implementation.
3. Passing tests do not substitute for realistic conditions. Confirm the tested path matches production shape — real data volume, concurrent access, network failure modes — not an idealized mock environment.

# Root Cause Over Shortcut

When an error, failure, or blocker arises mid-task, the default failure mode is fixing it just enough to get the immediate run to succeed, then moving on — without checking whether the fix survives production conditions. This is prohibited. The goal when debugging is never "make the error go away"; it is "resolve why the error happened, in a way that holds at scale, securely, cost-effectively, and reliably."

**Named shortcut patterns to refuse:**
- Increasing a timeout, retry count, or resource limit to mask a slow query, N+1 pattern, or unbounded operation, instead of fixing the underlying inefficiency.
- Disabling, weakening, or bypassing auth, CORS, input validation, or a security check to unblock a request, instead of configuring it correctly.
- Catching an exception and swallowing/logging it silently so the flow "completes," instead of handling or surfacing the actual failure.
- Hardcoding a value, ID, or response to route around a failing integration or dependency, instead of fixing the integration.
- Removing or loosening a rate limit, connection pool cap, or concurrency guard because it caused a failure under load, instead of designing for the load.
- A fix that only works for a single instance, single user, or single request, and would break or race under concurrent/multi-instance production conditions.
- An in-memory or local-file workaround for something that needs to survive a restart, scale horizontally, or be shared across instances (e.g., in-memory session state, local queues, non-persistent caches used as the source of truth).
- Any workaround that trades away one of scalability, security, cost, availability, or latency to make the other one look solved in the moment.

**Required response when a shortcut is tempting:**
- State the actual root cause once it's identified, even if fixing it takes longer or is out of scope for this session.
- If the correct fix requires infrastructure changes, access the agent doesn't have, or a decision only the developer can make, disclose that explicitly as a blocker — do not apply a local workaround and report the error as resolved.
- If a temporary workaround is genuinely necessary to keep moving, it must be labeled as a workaround with the real fix named, not folded into a "fixed" or "done" report.

# Anti-Rationalization Clause

Under context pressure, time pressure, or token budget pressure, the following reasoning is explicitly disallowed as a basis for marking something complete:
- "This edge case is unlikely" → still gate it, or disclose it as skipped.
- "I'll leave this for a follow-up" → acceptable only if stated to the user, not decided silently.
- "The code looks correct so it probably works" → not verification; report as Assumed, not Verified.
- "One test passed so the feature works" → one pass is a data point, not proof of correctness at scale.
- "It works now, the error is gone" → not the same as fixed; confirm the root cause was addressed, not masked.
- "This will work fine for now / at our current scale" → does not satisfy a production-grade requirement unless the developer explicitly said current scale is the bar.
- "This is just to get it running locally" → a local-only fix must be disclosed as such, not presented as the production fix.

Scope-cutting is not a violation of this skill. Silent, undisclosed scope-cutting is.

# Iconography and Typography Standard

Emoji anywhere in UI chrome or frontend component code is a demo-ism, not a production pattern — it signals prototype rather than an enterprise-grade shipped product, and renders inconsistently across platforms/fonts.

**Two separate things, not to be conflated:**
- **App-generated AI content** (e.g., a chatbot's message text, an LLM-generated response rendered as content within the app) is unrestricted. If an emoji shows up because the AI feature naturally produced one in its response text, that's content, not UI, and this rule does not apply to it and should not be filtered or enforced against it.
- **UI chrome built by the coding agent** — buttons, icons, nav items, status indicators, badges, logos, empty states, toasts/notifications, headings, labels, placeholder text, seed/sample copy, or any other UI element the agent authors as part of the interface — must never use emoji, full stop. This is interface construction, not content, and it's where the enterprise-grade bar applies.

**Enforcement for UI chrome:**
- **Use Material Symbols/Material Icons (Google Fonts)** for all iconography. Load via the Material Symbols font (`<link>` to `fonts.googleapis.com/icon?family=Material+Symbols...` or the equivalent icon font/component library) and reference icons by name/ligature or the corresponding component — never an emoji character.
- **Use Google Fonts for typography** rather than system-default or placeholder fonts, consistent with a real design system rather than an unstyled prototype.
- Logos and brand marks are custom assets or proper icon-library glyphs — never an emoji standing in for one.
- This ban is scoped to UI/frontend code the coding agent authors. It does not restrict emoji in the agent's own conversational replies to the developer, nor in AI-generated content the app itself produces and displays to end users.

---

# UI Copy Standard: Represent the User's Model, Not the System's

An interface should represent the user's mental model of their task — what they're trying to do and what happened as a result — not the engineer's mental model of how the system is built. Every piece of copy and every status element in the shipped UI should be written as if the person reading it has no idea what stack, vendor, or architecture sits behind it, because they don't, and it isn't their job to.

**The test to apply to any UI text or status element:**
1. **Would this make sense to someone with zero knowledge of how the app was built?** If it names a technology, protocol, vendor, database, or internal system state, it fails this test regardless of how accurate or well-intentioned it is.
2. **Does this change what the user does next?** If knowing it doesn't affect a decision or action available to the user, it doesn't belong in the interface — it belongs in a log, not a screen. If it does affect their next action, the copy should say the action-relevant consequence, not the internal cause.

Anything that fails either question gets rewritten or removed before it ships. This single test covers cases beyond the obvious ones — it's not a list to check against, it's the reasoning to apply to any copy or status indicator, including ones not enumerated below.

**What this produces in practice (illustrative, not exhaustive):**
- A label names the user's action or object (`Send`, `Repeat`, `Devices`, `Save`, `Retry`), not the mechanism behind it.
- A status shown to the user states the outcome and, if relevant, what they should do — never the vendor, service, or protocol producing that state. "You're offline — changes will sync when you're back" passes the test (actionable, no system detail); "WebSocket disconnected, reconnecting..." fails it.
- An error tells the user what happened to their action and what they can try, never which internal call or subsystem failed. "Couldn't send your message. Try again." passes; "POST /api/messages returned 500" fails.
- Decorative technical indicators built to visualize system health for the person who built it — connection dots, version badges, raw service-status widgets — fail question 2 outright: they don't change what the user does, so they don't belong in the shipped interface at all.

**Exception:** attribution that's contractually required by a vendor's terms of service (a mandated "Powered by X" badge on a paid API's free tier, for example) is a compliance obligation, not a design choice. Apply it only where required, and don't let a genuine legal requirement become an excuse to expose unrelated infrastructure elsewhere in the product.
