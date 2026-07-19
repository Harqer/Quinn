package com.musically.studio.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SpotifyColorScheme = darkColorScheme(
    primary = SpotifyGreen,
    secondary = SpotifyLightGray,
    tertiary = SpotifyGreen,
    background = SpotifyBlack,
    surface = SpotifyBlack,
    onPrimary = SpotifyBlack,
    onSecondary = SpotifyWhite,
    onTertiary = SpotifyBlack,
    onBackground = SpotifyWhite,
    onSurface = SpotifyWhite,
    surfaceContainerHigh = SpotifyDarkGray,
    surfaceContainerHighest = SpotifyLightGray
)

@Composable
fun MusicallyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled to enforce Spotify aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        SpotifyColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
