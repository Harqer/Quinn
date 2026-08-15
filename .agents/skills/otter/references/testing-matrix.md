# Testing Matrix & Edge Case Strategy

## 1. Unit & Component Test Coverage
- **ViewModel Coverage**: Test all public ViewModel methods, UI state flows, and error state transitions using `kotlinx-coroutines-test` and `Turbine`.
- **Backend Service Coverage**: Provide `_test.go` unit tests for Go handlers, middleware, and DataConnect clients using `net/http/httptest` and standard Go testing tools.
- **Cloud Functions Coverage**: Write Jest unit and integration tests for TypeScript Cloud Functions, HTTP endpoints, and AI agents.

## 2. Edge Condition Verification
- **Network Failures & Offlining**: Test component behavior under HTTP 500/503 errors, socket timeouts, cellular-to-WiFi switches, and offline network state transitions.
- **Null & Empty Responses**: Verify UI and state handling when API endpoints return empty lists (`[]`), null fields, or empty string queries.
- **Hardware Disconnections**: Test Wearable/Bluetooth disconnects mid-stream (glasses folded, powered off, out of range) to ensure non-silent, graceful fallback.

## 3. Assertion Quality & Test Hygiene
- **Non-Brittle Assertions**: Avoid fragile string matching or raw hardcoded route assertions. Assert semantic state transitions and strict model equality.
- **Deterministic CI Execution**: Do not silently swallow test failures or skip tests in CI when environment credentials are missing; use mock providers or explicit skipped assertions.
