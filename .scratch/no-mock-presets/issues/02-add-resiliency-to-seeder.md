# 02 — Add resiliency to seeder

**What to build:** The `seed_presets.ts` script should gracefully handle generation failures, timeouts, and rate limits without crashing the entire seeding process or leaving the database in an inconsistent state.

**Blocked by:** 01-refactor-seed-presets-to-api

**Status:** ready-for-agent

- [ ] Implement exponential backoff for the Cloud Function calls.
- [ ] Handle errors cleanly so that if a single track generation fails, the script continues to the next track.
- [ ] Ensure that no partial tracks (e.g. tracks without a cover or without audio) are written to the database if the generation fails halfway through.
