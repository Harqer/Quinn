package com.musically.studio.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector

data class SubscriptionTier(
    val productId: String,
    val name: String,
    val price: String,
    val billingPeriod: String = "/ mo",
    val features: List<String>,
    val badge: String? = null,
    val isHighlighted: Boolean = false,
)


data class FaqItem(val question: String, val answer: String)


data class FeatureHighlight(val icon: ImageVector, val title: String, val description: String)


