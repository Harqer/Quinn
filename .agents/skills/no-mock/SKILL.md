---
name: no-mock
description: Enforces production fallback strategies, avoiding mock data and dummy fallbacks in favor of explicit error states, skeleton loaders, and telemetry.
---

# Production Fallback Strategy

In a production environment, returning dummy or mock values masks real failures, exposes incorrect state to users, and obscures API errors. Instead of dummy fallbacks, production code should use:

1. **Explicit Error & Empty States**: Render actionable UI banners (`<ErrorAlert message="..." onRetry={...} />`) or empty state components (`<EmptyState />`) that allow users to retry.
2. **Skeleton Loading UI**: Display skeleton loaders or progress indicators while network requests are pending.
3. **Type-Safe Nullable / Empty Initialization**: Initialize data structures to empty values (`[]`, `null`) handled cleanly by the view layer.
4. **Telemetry & Error Logging**: Log network exceptions to monitoring platforms (e.g., Sentry) with structured metadata.
5. **Retry Policies**: Apply exponential backoff retries for transient API network failures.

## Enforcement Guidelines

* **No Mock Implementations**: Directs the agent to wire frontend components directly to real endpoints, backend services, or strongly-typed API contracts using HTTP clients (`fetch`, `axios`, `React Query`, `SWR`).
* **Production Fallbacks**: Prohibits dummy records and fake arrays in production code paths in favor of empty states, skeleton screens, telemetry logging, and retry logic.
