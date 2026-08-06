---
name: edge-to-edge
description: Guidelines for Android color and Material 3 (HCT, Dynamic color, Surface colors, etc.) for mobile design. Trigger this when styling, theming, or working with dynamic colors.
---

# Android color for mobile design

Use color to express style and communicate meaning. Setting your app's colors can be crucial for personalization, defining semantic purpose, and defining brand identity.

## Takeaways
- Check color contrast and avoid pairing colors with similar tones.
- Practice using colors meaningfully: stick to a palette. Extending your scheme with too many semantic colors can be confusing.
- If using semantic colors, use consistent colors.
- Build a light and dark color scheme (and ideally contrast themes).
- Assign colors with tokens to indicate the element's color role, instead of using a hardcoded value.
- Colors can come from various dynamic and static sources, but avoid mixing too many within the same view.

## Color Space: HCT (Hue, Chroma, and Tone)
Material 3 (M3) introduced HCT, a new color space that uses hue, chroma, and tone to define colors that are perceptually accurate:
- **Hue**: Analogous to the adjective an individual user might use to describe the color ("red", "violet"). Value ranges from 0-360.
- **Chroma**: Represents the colorfulness, ranging from neutral gray to full vibrancy (max ~120).
- **Tone**: Luminance, or brightness. HCT uses tone to create contrast.

## Color Scheme Process
The M3 color system translates a source color into five key colors: primary, secondary, tertiary, neutral, and neutral variant. These create tonal palettes.
- **Accent colors**: Primary, secondary, tertiary. Used for branding, highlighting actions, personal expression.
- **Semantic colors**: Colors with specific meaning (e.g., Error is red). Be consistent with meaning.
- **Surface colors**: Background elements such as component containers, sheets, and panes. Tonal surfaces create depth and contrast.

## Dynamic Color
Dynamic color (Android 12+) lets the system extract a source color from a user's wallpaper or in-app content. 
- Always provide a static fallback scheme if dynamic color isn't available.

## Implementing Color in Compose
- Use tokens instead of hard-coded values.
- M3 provides `MaterialTheme.colorScheme` to access the semantic and accent colors (e.g., `MaterialTheme.colorScheme.primary`, `MaterialTheme.colorScheme.surfaceVariant`).
- Color contrast is guaranteed by pairing the right container and on-container roles (e.g., `primaryContainer` and `onPrimaryContainer`).
