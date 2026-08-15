# Documentation & API Standards

## 1. Code & Package Documentation
- **GoDoc (Go)**: All exported Go packages, structs, methods, and functions must have top-level GoDoc comments explaining purpose, parameters, and concurrency guarantees.
- **KDoc (Kotlin)**: All public Kotlin classes, interfaces, ViewModels, and public extension methods must have KDoc blocks detailing behavior, exception contracts, and parameter types.
- **JSDoc (TypeScript)**: All Cloud Functions, callable endpoints, Genkit agents, and repository tools must have JSDoc blocks defining input schemas, authentication prerequisites, return payloads, and error codes.

## 2. Stale Code & Comment Hygiene
- **No Dead Comments**: Never leave empty callback comments (e.g., `// TODO: log or handle error`) or commented-out blocks in production code. Replace empty handlers with explicit error logging (`Timber.e(...)`, `slog.Error(...)`, or `logger.error(...)`).
- **No Outdated TODOs**: Remove stale TODO comments or resolve them before committing.

## 3. Public API & Architectural Decision Records (ADRs)
- **API Request/Response Contracts**: Document all HTTP REST, WebSocket, and GraphQL endpoint request and response schemas explicitly.
- **Architectural Trade-offs**: Document security or performance trade-offs (such as client-side billing UX guardrails vs server-side Cloud Function verification, or WebSocket CORS policies) in code comments or reference documents.
