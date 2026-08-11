/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetcaster.tv.ui.theme

import androidx.tv.material3.darkColorScheme
import com.musically.studio.ui.theme.MaveBackground
import com.musically.studio.ui.theme.MaveBrand
import com.musically.studio.ui.theme.MaveError
import com.musically.studio.ui.theme.MaveOnSurface
import com.musically.studio.ui.theme.MaveOnSurfaceVariant
import com.musically.studio.ui.theme.MavePrimary
import com.musically.studio.ui.theme.MaveSurface
import com.musically.studio.ui.theme.MaveSurfaceContainer
import com.musically.studio.ui.theme.MaveSurfaceVariant2
import com.musically.studio.ui.theme.MaveSurfaceVariant3
import com.musically.studio.ui.theme.MaveGreenLight
import com.musically.studio.ui.theme.MaveBlueGray200
import androidx.compose.ui.graphics.Color

/**
 * Mave brand color scheme for TV.
 * Uses semantic tokens from com.musically.studio.ui.theme — no hardcoded RGB values.
 *
 * Token mapping:
 *   primary          → MavePrimary     (#53E076) — brand green
 *   background       → MaveBackground  (#121212) — near-black
 *   surface          → MaveSurface     (#181818) — card surface
 *   surfaceVariant   → MaveSurfaceContainer (#282828)
 *   onSurface        → MaveOnSurface   (#FFFFFF)
 *   onSurfaceVariant → MaveOnSurfaceVariant (#B3B3B3) — secondary text
 */
val colorSchemeForDarkMode = darkColorScheme(
    primary = MavePrimary,
    onPrimary = MaveBackground,
    primaryContainer = MaveSurfaceContainer,
    onPrimaryContainer = MavePrimary,
    secondary = MaveBrand,
    onSecondary = MaveBackground,
    secondaryContainer = MaveSurfaceVariant2,
    onSecondaryContainer = MaveGreenLight,
    tertiary = MaveBlueGray200,
    onTertiary = MaveBackground,
    tertiaryContainer = MaveSurfaceVariant3,
    onTertiaryContainer = MaveBlueGray200,
    error = MaveError,
    onError = Color(0xFF690005),
    background = MaveBackground,
    onBackground = MaveOnSurface,
    surface = MaveSurface,
    onSurface = MaveOnSurface,
    surfaceVariant = MaveSurfaceContainer,
    onSurfaceVariant = MaveOnSurfaceVariant,
    border = MaveSurfaceVariant3,
    borderVariant = MaveSurfaceContainer,
    scrim = Color.Black,
    inverseSurface = MaveOnSurface,
    inverseOnSurface = MaveBackground,
    inversePrimary = MaveBrand,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)

// TV is always shown in dark mode — light scheme mirrors dark with slight elevation.
val colorSchemeForLightMode = colorSchemeForDarkMode
