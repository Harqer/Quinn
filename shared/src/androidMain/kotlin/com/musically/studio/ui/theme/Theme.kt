package com.musically.studio.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MaveColorScheme = darkColorScheme(
    primary = MavePrimary,
    onPrimary = MaveBackground,
    secondary = MaveOnSurfaceVariant,
    background = MaveBackground,
    surface = MaveBackground,
    onBackground = MaveOnSurface,
    onSurface = MaveOnSurface,
    error = MaveError,
    surfaceContainerHigh = MaveSurfaceContainer,
    surfaceContainerHighest = MaveOnSurfaceVariant
)

val LocalMaveColorScheme = staticCompositionLocalOf { MaveColorScheme }

@Composable
fun MaveAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled to enforce brand aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (dynamicColor) {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        MaveColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
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
