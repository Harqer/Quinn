# Implementation Plan: Production-Ready Frontend & Backend Orchestration (v3.3)

This plan ensures all frontend screens and components are fully wired to the backend, eliminates remaining mocks, and implements robust empty states and atomic design consistency.

## User Review Required

> [!IMPORTANT]
> **Full Registration Loop**: I am implementing the logic to actually *save* the data collected during the 10-step onboarding (Birthday, Gender, Artist Preferences) into Firestore.
> **Atomic design enforcement**: I will audit every screen to ensure they use the `MaveButton`, `MaveTextField`, and `MaveLogo` atoms instead of standard Material components.
> **Backend Steering handlers**: I will implement the missing WebSocket handlers for Skip, Seek, Shuffle, and Repeat in the `MusicService`.

## Proposed Changes

### 1. Backend: User Profile & Auth Re-Engineering

#### [NEW] [UserRepository.ts](file:///home/shaolin/lyria/src/repositories/UserRepository.ts)
- Implement `createProfile(uid, data)` to store birthday, gender, and name.
- Implement `updatePreferences(uid, artists)` to store the initial "Mave Vibe" seeds.

#### [NEW] [auth.ts](file:///home/shaolin/lyria/src/routes/auth.ts)
- Create routes for `POST /api/auth/profile` and `POST /api/auth/preferences`.
- Wire into `src/routes/index.ts`.

---

### 2. MainViewModel: Robust Registration State

#### [MODIFY] [MainViewModel.kt](file:///home/shaolin/lyria/app/src/main/java/com/musically/studio/ui/MainViewModel.kt)
- Add a `RegistrationState` flow to accumulate onboarding data across multiple screens.
- Implement `completeRegistration()`:
    1. Triggers `FirebaseAuth.createUserWithEmailAndPassword`.
    2. Syncs metadata to the new backend auth routes.
- Implement `saveArtistPreferences()`: Synchronizes the chosen 3+ artists to define the user's initial musical universe.

---

### 3. Android: Step-by-Step Wiring

#### [MODIFY] Onboarding Screens
- Update `EmailInputScreen.kt`, `PasswordInputScreen.kt`, `BirthdayInputScreen.kt`, `GenderInputScreen.kt`, and `NameTermsScreen.kt` to push data into the `MainViewModel`'s registration state.

#### [MODIFY] [LibraryScreen.kt](file:///home/shaolin/lyria/app/src/main/java/com/musically/studio/ui/screens/LibraryScreen.kt)
- Add a `MaveEmptyLibrary` molecule with a high-fidelity "Strike your first vibe" button to encourage creation.

---

### 4. Backend: Completing the Control Loop

#### [MODIFY] [MusicService.ts](file:///home/shaolin/lyria/src/services/MusicService.ts)
- Implement handlers for `skip_next`, `skip_previous`, `seek_to`, `toggle_shuffle`, `toggle_repeat`.
- Map these commands to the active Lyria `lyria-realtime-exp` session.

## Verification Plan

### Registration Audit
- **Onboarding Flow**: Complete all steps -> Verify user is created in Firebase Auth and metadata (Birthday/Gender) exists in Firestore.
- **Preference Sync**: Choose 3 artists -> Verify "Mave Welcome" state on Home suggests vibes similar to those artists.

### Playback Audit
- [x] **Shuffle/Repeat**: Toggle on Android -> Verify backend session config updates.
- [x] **Atomic Audit**: Verify no screen file uses `OutlinedTextField` or `Button` directly; all must use `MaveTextField` and `MaveButton`.
