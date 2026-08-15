# Security Guidelines & Defensive Hygiene

## 1. Secrets Management & Hardcoded Credentials
- **Zero Secrets in Source**: No API keys, JWT secrets, passwords, database URIs, or bearer tokens in `.kt`, `.go`, `.ts`, or `.java` files.
- **Environment & Secret Manager**: Store secrets in Cloud Secret Manager (Firebase/GCP), `.env` files (excluded via `.gitignore`), or `local.properties`.
- **Client Bundling**: Never bundle server-side keys (Gemini Lyria, Stripe private keys, database admin tokens) in mobile APK/AAB or frontend clients.

## 2. Injection Risks
- **SQL / DataConnect Injection**: Always use parameterized queries or GraphQL variables. Never concatenate strings into raw SQL/GraphQL queries.
- **Command & Path Traversal**: Validate and sanitize user-provided file paths using `path.Clean` (Go), `Path.normalize()` / `File.canonicalPath` (Kotlin), or `path.basename()` (TypeScript).

## 3. Authentication & Authorization
- **Token Validation**: Ensure all backend REST/WebSocket endpoints explicitly validate Firebase Auth ID Tokens (`auth.VerifyIDToken`).
- **CORS Configuration**: Restrict CORS origins in production Cloud Functions and Cloud Run services; do not leave `Access-Control-Allow-Origin: *` open for sensitive endpoints.
- **Role-Based Access (RBAC)**: Verify user ownership (`request.auth.uid == resource.data.userId`) before mutating document data.

## 4. Input Validation & Data Handling
- **Structured Validation**: Parse and validate all client payloads using Zod schemas (TypeScript), Go struct validators, or Kotlin contracts.
- **Safe Parsing**: Avoid unsafe reflection or deserialization of untrusted byte streams without schema boundaries.
