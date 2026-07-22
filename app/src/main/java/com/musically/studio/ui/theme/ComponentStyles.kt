package com.musically.studio.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.style.Style

/**
 * Unified Component Styles for Mave Studio.
 * Uses the experimental Compose Styles API for semantic design tokens.
 */
object MaveStyles {
    val maveButtonStyle = Style {
        // Default Button Style
        shape(RoundedCornerShape(28.dp))
        minHeight(56.dp)
        
        // Using State-based styling as supported by foundation.style.Style
        // If these specific attributes are not in alpha01, we use generic ones.
    }

    val maveTextFieldStyle = Style {
        shape(RoundedCornerShape(8.dp))
    }

    val maveCardStyle = Style {
        shape(RoundedCornerShape(16.dp))
        background(MaveSurfaceContainer)
    }
}
