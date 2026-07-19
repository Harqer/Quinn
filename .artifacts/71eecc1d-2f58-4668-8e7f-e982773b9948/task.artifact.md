# Task: Codebase Upgrade & CI Hardening

- `[/]` Dependency Upgrades
    - `[ ]` Update `package.json` with latest stable versions
    - `[ ]` Update GitHub Actions to use `pnpm`
- `[ ]` Code Hardening (Zero-Stub)
    - `[ ]` Replace `spotify:track:placeholder` in `PodcastView.tsx`
- `[ ]` Verification & Lockfile sync
    - `[ ]` Run `pnpm install`
    - `[ ]` Run `pnpm exec tsc --noEmit`
    - `[ ]` Run `pnpm test`
    - `[ ]` Run `./gradlew :app:lintDebug`
- `[ ]` Final Cleanup & Push
    - `[ ]` Commit and push to `origin main`
