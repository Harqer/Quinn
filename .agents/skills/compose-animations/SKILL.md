---
name: compose-animations
description: Use when building, debugging, or optimizing Jetpack Compose animations based on the official decision tree. Trigger when the user mentions 'animations', 'animate', 'AnimatedVisibility', 'transition', or wants to add motion to their Android UI.
---

# Jetpack Compose Animations Guide

Follow this structured decision tree to choose the appropriate animation API for your use case in Jetpack Compose:

## 1. Is your animation art-based (that is, SVGs or images)?
- **Yes**: Does it use simple SVGs (that is, an icon with micro-animations)?
  - **Yes**: Use `AnimatedVectorDrawable`.
  - **No**: Use a Third-party animation framework, for example, `Lottie`.
- **No**: Proceed to the next question.

## 2. Does the animation need to repeat infinitely?
- **Yes**: Use `rememberInfiniteTransition`.
- **No**: Proceed to the next question.

## 3. Are you animating a layout?
- **Yes**: Are you switching between composables with different content?
  - **Yes**: Are you using Navigation-Compose?
    - **Yes**: Use `composable()` with `enterTransition` and `exitTransition` set.
    - **No**: Use `AnimatedContent`, `Crossfade`, or `Pager`.
  - **No**: Are you animating the appearance or disappearance of content?
    - **Yes**: Use `AnimatedVisibility` or `animateFloatAsState` with `Modifier.alpha()`.
    - **No**: Are you animating a size change?
      - **Yes**: Use `Modifier.animateContentSize`.
      - **No**: Are you animating another layout property (for example, offset or padding)?
        - **Yes**: See "Are the properties completely independent of each other?" below.
        - **No**: Are you animating list items?
          - **Yes**: Use `animateItem()`.
- **No**: Proceed to the next question.

## 4. Are you animating multiple properties?
- **Yes**: Are the properties completely independent of each other?
  - **Yes**: Use `animate*AsState`. For Text, use `TextMotion.Animated`.
  - **No**: Do they need to start at the same time?
    - **Yes**: Use `updateTransition` with `AnimatedVisibility`, `animateFloat`, `animateInt`, etc.
    - **No**: Use `Animatable` with `animateTo`, called with different timings using suspend functions.
- **No**: Proceed to the next question.

## 5. Does the animation have predefined target values?
- **Yes**: Use `animate*AsState`. For Text, use `TextMotion.Animated`.
- **No**: Is the animation gesture-driven and the single source of truth?
  - **Yes**: Use `Animatable` with `animateTo` / `snapTo`.
  - **No**: Is it a one-shot animation without state management?
    - **Yes**: Use `AnimationState` or `animate`.
    - **No**: If the answer is not here, you may need a custom implementation or to reconsider the animation approach.

## Important Note for Text Animations
When animating text styles or sizes (using `animate*AsState` on typography attributes), always prefer using `TextMotion.Animated` within your `TextStyle` to ensure smooth text scaling and rendering transitions without snap artifacts.
