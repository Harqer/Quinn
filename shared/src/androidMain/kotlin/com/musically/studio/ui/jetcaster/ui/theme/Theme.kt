/*
 * Copyright 2020 The Android Open Source Project
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

package com.musically.studio.ui.jetcaster.ui.theme

import android.os.Build

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.musically.studio.ui.jetcaster.core.designsystem.theme.JetcasterShapes
import com.musically.studio.ui.jetcaster.core.designsystem.theme.JetcasterTypography
import com.musically.studio.ui.jetcaster.core.designsystem.theme.backgroundDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.backgroundDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.backgroundDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.backgroundLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.backgroundLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.backgroundLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.errorContainerDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.errorContainerDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.errorContainerDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.errorContainerLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.errorContainerLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.errorContainerLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.errorDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.errorDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.errorDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.errorLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.errorLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.errorLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.inverseOnSurfaceDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.inverseOnSurfaceDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.inverseOnSurfaceDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.inverseOnSurfaceLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.inverseOnSurfaceLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.inverseOnSurfaceLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.inversePrimaryDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.inversePrimaryDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.inversePrimaryDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.inversePrimaryLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.inversePrimaryLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.inversePrimaryLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.inverseSurfaceDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.inverseSurfaceDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.inverseSurfaceDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.inverseSurfaceLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.inverseSurfaceLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.inverseSurfaceLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onBackgroundDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onBackgroundDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onBackgroundDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onBackgroundLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onBackgroundLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onBackgroundLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onErrorContainerDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onErrorContainerDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onErrorContainerDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onErrorContainerLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onErrorContainerLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onErrorContainerLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onErrorDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onErrorDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onErrorDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onErrorLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onErrorLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onErrorLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onPrimaryContainerDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onPrimaryContainerDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onPrimaryContainerDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onPrimaryContainerLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onPrimaryContainerLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onPrimaryContainerLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onPrimaryDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onPrimaryDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onPrimaryDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onPrimaryLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onPrimaryLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onPrimaryLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSecondaryContainerDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSecondaryContainerDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSecondaryContainerDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSecondaryContainerLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSecondaryContainerLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSecondaryContainerLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSecondaryDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSecondaryDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSecondaryDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSecondaryLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSecondaryLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSecondaryLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSurfaceDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSurfaceDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSurfaceDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSurfaceLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSurfaceLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSurfaceLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSurfaceVariantDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSurfaceVariantDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSurfaceVariantDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSurfaceVariantLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSurfaceVariantLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onSurfaceVariantLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onTertiaryContainerDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onTertiaryContainerDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onTertiaryContainerDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onTertiaryContainerLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onTertiaryContainerLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onTertiaryContainerLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onTertiaryDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onTertiaryDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onTertiaryDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onTertiaryLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onTertiaryLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.onTertiaryLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.outlineDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.outlineDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.outlineDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.outlineLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.outlineLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.outlineLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.outlineVariantDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.outlineVariantDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.outlineVariantDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.outlineVariantLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.outlineVariantLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.outlineVariantLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.primaryContainerDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.primaryContainerDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.primaryContainerDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.primaryContainerLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.primaryContainerLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.primaryContainerLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.primaryDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.primaryDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.primaryDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.primaryLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.primaryLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.primaryLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.scrimDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.scrimDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.scrimDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.scrimLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.scrimLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.scrimLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.secondaryContainerDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.secondaryContainerDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.secondaryContainerDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.secondaryContainerLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.secondaryContainerLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.secondaryContainerLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.secondaryDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.secondaryDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.secondaryDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.secondaryLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.secondaryLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.secondaryLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceBrightDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceBrightDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceBrightDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceBrightLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceBrightLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceBrightLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerHighDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerHighDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerHighDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerHighLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerHighLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerHighLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerHighestDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerHighestDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerHighestDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerHighestLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerHighestLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerHighestLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerLowDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerLowDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerLowDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerLowLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerLowLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerLowLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerLowestDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerLowestDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerLowestDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerLowestLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerLowestLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceContainerLowestLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceDimDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceDimDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceDimDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceDimLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceDimLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceDimLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceVariantDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceVariantDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceVariantDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceVariantLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceVariantLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.surfaceVariantLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.tertiaryContainerDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.tertiaryContainerDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.tertiaryContainerDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.tertiaryContainerLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.tertiaryContainerLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.tertiaryContainerLightMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.tertiaryDark
import com.musically.studio.ui.jetcaster.core.designsystem.theme.tertiaryDarkHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.tertiaryDarkMediumContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.tertiaryLight
import com.musically.studio.ui.jetcaster.core.designsystem.theme.tertiaryLightHighContrast
import com.musically.studio.ui.jetcaster.core.designsystem.theme.tertiaryLightMediumContrast

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)


@Composable
fun JetcasterTheme(dynamicColor: Boolean = false, content: @Composable () -> Unit) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicDarkColorScheme(context)
        }

        else -> darkScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = JetcasterShapes,
        typography = JetcasterTypography,
        content = content,
    )
}
