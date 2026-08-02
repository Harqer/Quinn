---
name: semantic-functional-audit
description: Audits a screen or application feature to ensure its semantic purpose matches its actual functional implementation.
---

# Semantic Functional Auditing

Use this skill when tasked with evaluating whether a screen or feature is functionally complete based on its semantic meaning (e.g., verifying that a "Library" screen actually contains working bookmarking, liking, and playback features).

## Goal
To eliminate guesswork by ensuring that what the user *expects* to happen on a screen is completely and correctly mapped to the underlying code.

## The Audit Workflow

When asked to perform a semantic functional audit on a screen, follow these steps strictly:

### 1. Semantic Intent Inference
- Identify the name and primary goal of the screen (e.g., "LibraryScreen", "SettingsView").
- List the **implicit user expectations** for this type of screen. For example, a "Library" semantically implies the ability to view history, saved items (likes/bookmarks), and categorize content.

### 2. UI Component Mapping
- Scan the UI code (e.g., Jetpack Compose, React, HTML).
- Verify that UI components exist for every implicit expectation.
- Ensure that the terminology and icons used in the UI match real-world semantics (e.g., a heart icon for "Like").

### 3. State & Logic Wiring Verification
- Trace the UI interactions (onClick, onSwipe) to the state management layer (e.g., ViewModel, Redux, Provider).
- **Check for dead ends:** Ensure that clicking a button doesn't just trigger an empty function or a visual toggle without backend execution.

### 4. Persistence & API Validation
- Verify that the state manager properly communicates with the backend/API client to persist the action.
- Ensure that the screen can correctly fetch and display this persisted state upon reload.

### 5. Resolution & Reporting
- If any gaps are found (e.g., a "Share" button exists but doesn't trigger a native share intent, or a "Liked" filter exists but the API doesn't fetch liked tracks), immediately document the gap and propose the exact code changes needed to wire it up.
