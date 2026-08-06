package com.musically.studio.ui.theme

import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography

import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import com.musically.studio.ui.theme.MavePrimary
import com.musically.studio.ui.theme.MaveBackground
import com.musically.studio.ui.theme.MaveOnSurface
import com.musically.studio.ui.theme.MaveOnSurfaceVariant

object MaveStyles {
    val scaffoldStyle = Style {
        background(colors.background)
    }
    val maveButtonStyle = Style {
        background(colors.primary)
        shape(RoundedCornerShape(24.dp))
        minHeight(48.dp)
    }
    val outlinedButton = Style {
        background(Color.Transparent)
        minHeight(48.dp)
    }
    val maveCardStyle = Style {
        background(colors.surfaceContainerHigh)
        shape(RoundedCornerShape(12.dp))
    }
    val musicTrackCardStyle = Style {
        background(Color.Transparent)
        shape(RoundedCornerShape(8.dp))
    }
    val chatInputRowStyle = Style {
        background(colors.surfaceContainerHigh)
        shape(RoundedCornerShape(24.dp))
    }
    val sendButtonStyle = Style {
        background(colors.primary)
        shape(CircleShape)
    }
    val userMessageBubbleStyle = Style {
        background(colors.primary)
        shape(RoundedCornerShape(16.dp))
    }
    val aiMessageBubbleStyle = Style {
        background(colors.surfaceContainerHighest)
        shape(RoundedCornerShape(16.dp))
    }
    
    val currentDeviceCardStyle = Style {
        background(colors.primary.copy(alpha = 0.1f))
        shape(RoundedCornerShape(16.dp))
    }
    val deviceCardStyle = Style {
        background(colors.surfaceContainerHigh)
        shape(RoundedCornerShape(16.dp))
    }
    val premiumPlanCardStyle = Style {
        background(colors.surfaceContainerHigh)
        shape(RoundedCornerShape(16.dp))
    }
    val premiumBadgeStyle = Style {
        background(colors.primary)
        shape(RoundedCornerShape(12.dp))
    }
    val premiumCTAButtonStyle = Style {
        background(colors.primary)
        shape(RoundedCornerShape(24.dp))
        minHeight(48.dp)
    }

    val primaryButton = Style {}
    val playbackBarStyle = Style {}
    val maveTextFieldStyle = Style {}
    val categoryGridItemStyle = Style {}
    val largePodcastCardStyle = Style {}
    val listRowItemStyle = Style {}
    val libraryRowItemStyle = Style {}
    val filterPillStyle = Style {}
}

val StyleScope.colors: ColorScheme
    get() = LocalMaveColorScheme.currentValue

val StyleScope.typography: Typography
    get() = com.musically.studio.ui.theme.Typography
