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

val SUBSCRIPTION_TIERS = listOf(
    SubscriptionTier(
        productId = "premium_basic",
        name = "Basic Creator",
        price = "$20",
        features = listOf(
            "30 AI-generated songs",
            "60 min Real-time sessions",
            "50 Cover images",
            "10 Music videos",
        ),
    ),
    SubscriptionTier(
        productId = "premium_pro",
        name = "Pro Studio",
        price = "$50",
        features = listOf(
            "100 AI-generated songs",
            "150 min Real-time sessions",
            "200 Cover images",
            "40 Music videos",
            "Commercial use license",
            "Priority generation queue",
        ),
        badge = "MOST POPULAR",
        isHighlighted = true,
    ),
    SubscriptionTier(
        productId = "premium_ultra",
        name = "Ultra Unlimited",
        price = "$100",
        features = listOf(
            "Unlimited AI-generated songs",
            "Unlimited Real-time sessions",
            "Unlimited Cover images & videos",
            "Commercial use license",
            "Highest priority queue",
            "Dedicated support channel",
        ),
    ),
)

data class FaqItem(val question: String, val answer: String)

val FAQ_ITEMS = listOf(
    FaqItem(
        question = "Can I cancel anytime?",
        answer = "Yes — cancel from your Google Play subscriptions page or through this app at any time. You keep access until the end of your current billing period."
    ),
    FaqItem(
        question = "What is Real-time session time?",
        answer = "Real-time sessions let Mave generate music live while it processes your camera or voice. Your monthly allocation resets on your billing date."
    ),
    FaqItem(
        question = "Does commercial use apply to all tiers?",
        answer = "Commercial use is included in Pro Studio and Ultra Unlimited plans. Basic Creator songs are for personal use only."
    ),
    FaqItem(
        question = "What happens if I downgrade?",
        answer = "Your existing songs are always yours. Downgrading affects future generation limits, not content you've already created."
    ),
)

data class FeatureHighlight(val icon: ImageVector, val title: String, val description: String)

val FEATURE_HIGHLIGHTS = listOf(
    FeatureHighlight(
        icon = Icons.Default.MusicNote,
        title = "Lyria 3 & Magenta RT",
        description = "Google's most advanced music generation models, available exclusively on Mave."
    ),
    FeatureHighlight(
        icon = Icons.Default.VideoLibrary,
        title = "AI Cover Art & Video",
        description = "Generate professional cover images and music videos directly from your sessions."
    ),
    FeatureHighlight(
        icon = Icons.Default.Star,
        title = "Real-time with Glasses",
        description = "Stream AI music live from Meta Ray-Ban glasses — the world's first ambient music experience."
    ),
    FeatureHighlight(
        icon = Icons.Default.Lock,
        title = "Commercial License",
        description = "Pro and Ultra plans include a commercial use license. Publish and monetize your tracks freely."
    ),
)
