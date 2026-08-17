# No-Mock Policy

**Rule**: Never fallback to mock data, stub endpoints, or silent default states.

## Principles
1. **Zero Silent Fallback Mocks**: If an API integration (payment processing, music generation, audio stream, database lookup, or AI tool execution) fails, the application MUST NEVER silently substitute fake mock data, fake transaction confirmations, fake credit balances, or hallucinated outputs.
2. **Transparent Error Signaling**: When a network, API, or service error occurs, surface explicit, user-understandable failure messaging with typed error states.
3. **Actionable Recovery Paths**: Always provide actionable recovery options for the user (e.g. Retry, Edit Prompt, Re-authenticate) instead of dead ends or silent mock defaults.
4. **Observable Error Telemetry**: Log every failure and fallback attempt with structured telemetry (`Timber.e(...)`, `console.error(...)`) to ensure real-time alerting on backend degradation.
