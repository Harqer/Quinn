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
import com.google.android.material.color.MaterialColors

import com.google.android.material.color.utilities.Scheme

// Use the perceptual color model (HCT) to generate a fully harmonized dark theme 
// based on the brand's primary seed color. This builds a tonal palette where all 
// surfaces, accents, and text colors are in perceptual harmony.
@android.annotation.SuppressLint("RestrictedApi")
private fun createHarmonizedScheme(): ColorScheme {
    val seedArgb = MavePrimary.toArgb()
    val scheme = Scheme.dark(seedArgb)
    return darkColorScheme(
        primary = Color(scheme.primary),
        onPrimary = Color(scheme.onPrimary),
        primaryContainer = Color(scheme.primaryContainer),
        onPrimaryContainer = Color(scheme.onPrimaryContainer),
        secondary = Color(scheme.secondary),
        onSecondary = Color(scheme.onSecondary),
        secondaryContainer = Color(scheme.secondaryContainer),
        onSecondaryContainer = Color(scheme.onSecondaryContainer),
        tertiary = Color(scheme.tertiary),
        onTertiary = Color(scheme.onTertiary),
        tertiaryContainer = Color(scheme.tertiaryContainer),
        onTertiaryContainer = Color(scheme.onTertiaryContainer),
        error = Color(scheme.error),
        onError = Color(scheme.onError),
        errorContainer = Color(scheme.errorContainer),
        onErrorContainer = Color(scheme.onErrorContainer),
        background = Color(scheme.background),
        onBackground = Color(scheme.onBackground),
        surface = Color(scheme.surface),
        onSurface = Color(scheme.onSurface),
        surfaceVariant = Color(scheme.surfaceVariant),
        onSurfaceVariant = Color(scheme.onSurfaceVariant),
        outline = Color(scheme.outline),
        inverseOnSurface = Color(scheme.inverseOnSurface),
        inverseSurface = Color(scheme.inverseSurface),
        inversePrimary = Color(scheme.inversePrimary),
        surfaceTint = Color(scheme.primary)
    )
}

private val MaveColorScheme = createHarmonizedScheme()

val LocalMaveColorScheme = staticCompositionLocalOf { MaveColorScheme }

// Material You Dynamic Color Harmonization
fun Color.harmonized(primary: Color): Color {
    return try {
        Color(MaterialColors.harmonize(this.toArgb(), primary.toArgb()))
    } catch (e: Exception) {
        // Fallback to simple RGB interpolation if MaterialColors is unavailable
        androidx.compose.ui.graphics.lerp(this, primary, 0.2f)
    }
}

@Composable
fun MaveAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Enabled Dynamic color to support Material You personalization
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val baseColorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        MaveColorScheme
    }

    // Harmonize semantic colors (like Error) with the primary dynamic color 
    // to maintain brand identity while blending with user's wallpaper hue.
    val colorScheme = baseColorScheme.copy(
        error = baseColorScheme.error.harmonized(baseColorScheme.primary)
    )
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
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
