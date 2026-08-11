/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetcaster.theme

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme
import com.musically.studio.ui.theme.MaveBackground
import com.musically.studio.ui.theme.MaveBrand
import com.musically.studio.ui.theme.MaveBlueGray200
import com.musically.studio.ui.theme.MaveBlueGray400
import com.musically.studio.ui.theme.MaveError
import com.musically.studio.ui.theme.MaveGreenLight
import com.musically.studio.ui.theme.MaveOnSurface
import com.musically.studio.ui.theme.MaveOnSurfaceVariant
import com.musically.studio.ui.theme.MavePrimary
import com.musically.studio.ui.theme.MaveSurface
import com.musically.studio.ui.theme.MaveSurfaceContainer
import com.musically.studio.ui.theme.MaveSurfaceVariant2
import com.musically.studio.ui.theme.MaveSurfaceVariant3
import com.musically.studio.ui.theme.MaveSurfaceVariant4

/**
 * Wear OS color scheme using Mave semantic brand tokens.
 *
 * No hardcoded RGB values — every slot maps to a named Mave token.
 *
 * Token mapping:
 *   primary / primaryDim  → MavePrimary     (#53E076)
 *   background            → MaveBackground  (#121212)
 *   surfaceContainer      → MaveSurfaceContainer (#282828)
 *   onSurface             → MaveOnSurface   (#FFFFFF)
 *   onSurfaceVariant      → MaveOnSurfaceVariant (#B3B3B3)
 *   secondary             → MaveBrand       (#1DB954)  — Spotify-green accent
 *   tertiary              → MaveBlueGray200            — neutral highlight
 */
internal val wearColorPalette: ColorScheme = ColorScheme(
    primary = MavePrimary,
    primaryDim = MaveBrand,
    onPrimary = MaveBackground,
    primaryContainer = MaveSurfaceContainer,
    onPrimaryContainer = MavePrimary,
    secondary = MaveBrand,
    secondaryDim = MaveBrand,
    onSecondary = MaveBackground,
    secondaryContainer = MaveSurfaceVariant2,
    onSecondaryContainer = MaveGreenLight,
    tertiary = MaveBlueGray200,
    onTertiary = MaveBackground,
    tertiaryContainer = MaveSurfaceVariant4,
    error = MaveError,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = MaveBackground,
    onBackground = MaveOnSurface,
    onSurface = MaveOnSurface,
    onSurfaceVariant = MaveOnSurfaceVariant,
    surfaceContainer = MaveSurfaceContainer,
    surfaceContainerLow = MaveSurface,
    surfaceContainerHigh = MaveSurfaceVariant3,
    outline = MaveSurfaceVariant3,
    outlineVariant = MaveSurfaceContainer,
)
