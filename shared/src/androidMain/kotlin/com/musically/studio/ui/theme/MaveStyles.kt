package com.musically.studio.ui.theme

import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography

object MaveStyles {
    val scaffoldStyle = Style {
        background(colors.background)
    }
    val primaryButton = Style {}
    val outlinedButton = Style {}
    val maveCardStyle = Style {}
    val playbackBarStyle = Style {}
    val maveButtonStyle = Style {}
    val maveTextFieldStyle = Style {}
    val chatInputRowStyle = Style {}
    val sendButtonStyle = Style {}
    val aiMessageBubbleStyle = Style {}
    val userMessageBubbleStyle = Style {}
    val musicTrackCardStyle = Style {}
    val currentDeviceCardStyle = Style {}
    val deviceCardStyle = Style {}
    val categoryGridItemStyle = Style {}
    val largePodcastCardStyle = Style {}
    val listRowItemStyle = Style {}
    val libraryRowItemStyle = Style {}
    val filterPillStyle = Style {}

    // Premium / Subscription screen styles
    val premiumPlanCardStyle = Style {}
    val premiumBadgeStyle = Style {}
    val premiumCTAButtonStyle = Style {}
}

val StyleScope.colors: ColorScheme
    get() = LocalMaveColorScheme.currentValue

val StyleScope.typography: Typography
    get() = com.musically.studio.ui.theme.Typography
