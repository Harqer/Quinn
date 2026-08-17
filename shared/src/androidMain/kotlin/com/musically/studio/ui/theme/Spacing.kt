/**
 * @AtomicLevel: Atom
 * @SemanticPurpose: Android Component for Spacing.kt
 */

package com.musically.studio.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class MaveSpacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val studioContent: Dp = 24.dp,
    val studioHeader: Dp = 20.dp
)

val LocalMaveSpacing = staticCompositionLocalOf { MaveSpacing() }
