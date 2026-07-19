# Final CI/CD Hardening & Alias Resolution Plan

This plan resolves the persistent "Unloadable Dependency" error in CI and ensures the **Musically** production pipeline is robust and fully synchronized with the repository's modular architecture.

## User Review Required

> [!IMPORTANT]
> **Alias Resolution**: I am switching the Vite configuration to use `vite-tsconfig-paths`. This ensures that the `@/*` aliases in `tsconfig.json` are perfectly mirrored in the build process, resolving the `No such file or directory` error found in the GitHub Action logs.

> [!NOTE]
> **CI Consistency**: The workflows are now locked to `pnpm install` and the established `pnpm-lock.yaml`, ensuring that the CI environment exactly matches the local development environment.

## Proposed Changes

### 1. Build Orchestration (Vite)

#### [MODIFY] [vite.config.ts](file:///home/shaolin/lyria/vite.config.ts)
- Integrate `vite-tsconfig-paths` as the primary resolution engine.
- Remove manual `resolve.alias` blocks to prevent "split-brain" configuration where Vite and TypeScript disagree on pathing.

---

### 2. Dependency Hardening

#### [MODIFY] [package.json](file:///home/shaolin/lyria/package.json)
- Ensure `vite-tsconfig-paths` is present in `devDependencies`.
- Confirm all peer dependencies for `react-native-css` and `nativewind` are satisfied by the latest stable versions.

---

### 3. Production Environment Sync

#### [VERIFY] GitHub Actions
- Ensure `firebase-hosting-merge.yml` and `firebase-hosting-pull-request.yml` are using `pnpm/action-setup@v4`.
- Confirm the `pnpm install` and `pnpm run build` sequence is clean.

## Verification Plan

### Automated Build Verification
- **Local Production Build**: Run `pnpm run build` -> Verify `dist/` is generated correctly with zero resolution warnings.
- **Path Resolution**: Confirm `@/web/App` resolves correctly during the transformation phase.

### Quality Audit
- [x] **No Loops**: This is a single, deterministic fix using established plugins rather than ad-hoc scripts.
- [x] **Zero-Stub**: Confirmed no mocks remain in the creation logic.

***

**Do you approve of this final resolution to fix the CI build and synchronize path aliases?**
