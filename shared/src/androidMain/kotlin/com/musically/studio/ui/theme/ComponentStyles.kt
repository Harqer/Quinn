package com.musically.studio.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.style.Style

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.Shapes
import androidx.compose.foundation.style.StyleScope

val StyleScope.colors: ColorScheme
    get() = LocalMaveColorScheme.currentValue

val StyleScope.typography: Typography
    get() = Typography

/**
 * Unified Component Styles for Mave Studio.
 * Uses the experimental Compose Styles API for semantic design tokens.
 */
object MaveStyles {
    val maveButtonStyle = Style {
        // Default Button Style
        shape(RoundedCornerShape(28.dp))
        minHeight(56.dp)
    }

    val primaryButton = Style {
        shape(RoundedCornerShape(28.dp))
        minHeight(56.dp)
        background(colors.primary)
    }

    val outlinedButton = Style {
        shape(RoundedCornerShape(28.dp))
        minHeight(56.dp)
        border(1.dp, colors.onBackground)
        background(Color.Transparent)
    }

    val maveTextFieldStyle = Style {
        shape(RoundedCornerShape(8.dp))
    }

    val maveCardStyle = Style {
        shape(RoundedCornerShape(16.dp))
        background(colors.surfaceContainerHigh)
    }

    val playbackBarStyle = Style {
        shape(RoundedCornerShape(8.dp))
        background(colors.surfaceContainerHighest)
        minHeight(64.dp)
    }

    val filterPillStyle = Style {
        shape(RoundedCornerShape(16.dp))
        background(Color.White.copy(alpha = 0.1f))
    }

    val largePodcastCardStyle = Style {
        shape(RoundedCornerShape(8.dp))
        background(Color.Transparent)
    }

    val listRowItemStyle = Style {
        background(Color.Transparent)
    }
    
    val libraryRowItemStyle = Style {
        background(Color.Transparent)
    }

    val categoryGridItemStyle = Style {
        shape(RoundedCornerShape(8.dp))
    }

    val userMessageBubbleStyle = Style {
        shape(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
        background(Color(0xFF2A2A2A))
    }

    val aiMessageBubbleStyle = Style {
        shape(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp))
        background(Color(0xFF1A1A1A))
    }
}
