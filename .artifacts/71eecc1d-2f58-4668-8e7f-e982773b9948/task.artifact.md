# Task: Codebase Upgrade & Dependency Hardening

- `[x]` AI Code Fixes
    - `[x]` Fix `src/services/quinn-graph.ts` (modelName -> model)
- `[/]` Dependency Upgrades
    - `[ ]` Update `package.json` with latest stable versions
    - `[ ]` Run `pnpm install` and verify `pnpm-lock.yaml`
- `[ ]` Verification
    - `[ ]` Run `pnpm exec tsc --noEmit`
    - `[ ]` Run `pnpm test`
    - `[ ]` Run Android linting and build
- `[ ]` Final Cleanup & Push
    - `[ ]` Commit and push to `origin main`
