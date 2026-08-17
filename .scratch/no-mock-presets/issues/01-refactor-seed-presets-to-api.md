# 01 — Refactor seed_presets to API

**What to build:** The `seed_presets.ts` script should be rewritten to invoke the backend `executeTool` endpoints (`generate_full_track` and `generate_cover_image`) for each fetched iTunes chart item, rather than directly writing mock URLs into the database.

**Blocked by:** None — can start immediately.

**Status:** ready-for-agent

- [ ] Fetch top iTunes charts as before.
- [ ] For each chart item, construct a prompt for track generation.
- [ ] Call the Firebase Cloud Function (`executeTool`) for `generate_full_track`.
- [ ] Call the Firebase Cloud Function (`executeTool`) for `generate_cover_image` using the Nano Banana 2 model.
- [ ] Verify that real AI tracks are written to the database (containing both `audioUrl` and `coverUrl`).
