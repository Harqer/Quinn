/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: Android Component for Theme.kt
 */

package com.musically.studio.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Material 3 Dark Color Scheme (HCT Tonal Palette derived from MavePrimary 0xFF53E076)
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF53E076),
    onPrimary = Color(0xFF003915),
    primaryContainer = Color(0xFF005322),
    onPrimaryContainer = Color(0xFF75FD90),
    secondary = Color(0xFFB9CCB8),
    onSecondary = Color(0xFF253427),
    secondaryContainer = Color(0xFF3B4B3C),
    onSecondaryContainer = Color(0xFFD5E8D4),
    tertiary = Color(0xFFA1CED6),
    onTertiary = Color(0xFF00363D),
    tertiaryContainer = Color(0xFF1F4D54),
    onTertiaryContainer = Color(0xFFBCEAE2),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF101411),
    onBackground = Color(0xFFE1E3DF),
    surface = Color(0xFF101411),
    onSurface = Color(0xFFE1E3DF),
    surfaceVariant = Color(0xFF414942),
    onSurfaceVariant = Color(0xFFC1C9BF),
    outline = Color(0xFF8B938A),
    outlineVariant = Color(0xFF414942),
    surfaceContainerLowest = Color(0xFF0B0F0C),
    surfaceContainerLow = Color(0xFF181C19),
    surfaceContainer = Color(0xFF1C201D),
    surfaceContainerHigh = Color(0xFF272B27),
    surfaceContainerHighest = Color(0xFF323632),
    inverseSurface = Color(0xFFE1E3DF),
    inverseOnSurface = Color(0xFF2D312E),
    inversePrimary = Color(0xFF006D2F)
)

// Material 3 Light Color Scheme (HCT Tonal Palette derived from MavePrimary 0xFF53E076)
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006D2F),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF75FD90),
    onPrimaryContainer = Color(0xFF00210A),
    secondary = Color(0xFF526353),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD5E8D4),
    onSecondaryContainer = Color(0xFF101F13),
    tertiary = Color(0xFF38656C),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFBCEAE2),
    onTertiaryContainer = Color(0xFF002025),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF6FBF4),
    onBackground = Color(0xFF181C19),
    surface = Color(0xFFF6FBF4),
    onSurface = Color(0xFF181C19),
    surfaceVariant = Color(0xFFDDE5DB),
    onSurfaceVariant = Color(0xFF414942),
    outline = Color(0xFF717972),
    outlineVariant = Color(0xFFC1C9BF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF0F5EE),
    surfaceContainer = Color(0xFFEAEFE8),
    surfaceContainerHigh = Color(0xFFE5EAE3),
    surfaceContainerHighest = Color(0xFFDFE4DD),
    inverseSurface = Color(0xFF2D312E),
    inverseOnSurface = Color(0xFFEFF2EC),
    inversePrimary = Color(0xFF53E076)
)

val LocalMaveColorScheme = staticCompositionLocalOf { DarkColorScheme }

@Composable
fun MaveAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Supports Material You dynamic color adaptation on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode && view.context is Activity) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalMaveSpacing provides MaveSpacing(),
        LocalMaveColorScheme provides colorScheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object MaveTheme {
    val spacing: MaveSpacing
        @Composable
        get() = LocalMaveSpacing.current

    val styles: MaveStyles = MaveStyles
}
