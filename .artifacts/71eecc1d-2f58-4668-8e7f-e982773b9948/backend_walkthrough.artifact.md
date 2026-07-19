# Backend Modernization Walkthrough

I have successfully refactored the Lyria backend from a monolithic `server.ts` into a modular, production-ready architecture. This change resolves several critical security and stability issues.

## Key Fixes

### 1. Secure WebSocket Proxy
The `/api/music/ws` endpoint is no longer open to the public.
- **Change**: Added a validation step in the `upgrade` handler.
- **Impact**: Only authenticated users with a valid Firebase ID token can establish a WebSocket connection for Gemini music generation, preventing API quota drain.

### 2. XSS Protection in Spotify OAuth
- **Change**: Wrapped the `error` query parameter in the `xss()` sanitizer before rendering the callback HTML.
- **Impact**: Prevents Reflected XSS attacks where malicious scripts could be injected via the URL.

### 3. Eliminated Initialization Race Conditions
- **Change**: Introduced an `async init()` sequence in `src/index.ts`.
- **Impact**: Ensures that secrets (like `GEMINI_API_KEY`) are fully resolved from Google Secret Manager before the AI services are initialized or the server begins accepting requests.

### 4. Stability Fixes
- **Backup Cron**: Removed the undeclared `useInMemoryDb` variable which was causing silent failures in the daily backup task.
- **Error Handling**: Implemented a centralized error handler in `src/app.ts` to prevent internal stack trace leakage in production.

## New Architecture

The backend is now organized into a logical directory structure:
- **`src/config/`**: Centralized service configurations (Firebase, etc.).
- **`src/middlewares/`**: Reusable logic for Auth, App Check, and Quota management.
- **`src/routes/`**: Feature-specific Express routers (`spotify`, `music`, `logs`).
- **`src/services/`**: Core business logic and external API integrations.
- **`src/schemas/`**: Strict data validation using Zod.

## Verification

### Security Pass
- [x] Unauthenticated WebSocket connections are now rejected with `401 Unauthorized`.
- [x] Input validation is enforced on all POST/GET endpoints.
- [x] API keys are protected from top-level initialization race conditions.

> [!TIP]
> To run the server now, use `npm start` which points to the thin wrapper at `server.ts` or directly execute `node src/index.js` (after transpilation).
