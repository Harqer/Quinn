# Task: Backend Architectural Refactor

- `[/]` Repository Layer Implementation
    - `[ ]` Create `src/repositories/SpotifyRepository.ts`
    - `[ ]` Create `src/repositories/QuotaRepository.ts`
    - `[ ]` Create `src/repositories/TrackRepository.ts`
- `[ ]` Service Layer Implementation
    - `[ ]` Create `src/services/MusicService.ts`
    - `[ ]` Refactor `src/services/spotify.ts` to use Repository
- `[ ]` Controller Layer Implementation
    - `[ ]` Refactor `src/routes/music.ts`
    - `[ ]` Refactor `src/middlewares/auth.ts`
- `[ ]` Final Cleanup
    - `[ ]` Remove direct database references from services and routes
    - `[ ]` Verify build and tests
