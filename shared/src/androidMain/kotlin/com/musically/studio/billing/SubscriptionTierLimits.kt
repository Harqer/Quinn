package com.musically.studio.billing

/**
 * Immutable limits for a given subscription tier.
 *
 * [Int.MAX_VALUE] is used as the sentinel for "unlimited" so callers can use a single
 * `usage < limit` comparison without special-casing the Ultra tier.
 */
data class TierLimits(
    /** Maximum AI-generated songs the user may create in a calendar month. */
    val songsPerMonth: Int,
    /** Maximum podcast episodes the user may generate in a calendar month. */
    val podcastEpsPerMonth: Int,
    /** Maximum real-time live-session minutes the user may consume in a calendar month. */
    val realtimeMinutesPerMonth: Int,
) {
    val songsUnlimited: Boolean get() = songsPerMonth == Int.MAX_VALUE
    val podcastsUnlimited: Boolean get() = podcastEpsPerMonth == Int.MAX_VALUE
    val realtimeUnlimited: Boolean get() = realtimeMinutesPerMonth == Int.MAX_VALUE
}

/**
 * Reason returned when a generation attempt is blocked by the quota system.
 * Used by the VM to emit the correct [UsageLimitBottomSheet] state.
 */
enum class GenerationBlockReason {
    SONGS_LIMIT_REACHED,
    PODCASTS_LIMIT_REACHED,
    REALTIME_LIMIT_REACHED,
}

/**
 * Maps a Play Billing [productId] to its [TierLimits].
 *
 * This is the single source of truth for subscription entitlements on the Android client.
 * Any change to limits here must be mirrored in the backend quota-enforcement layer
 * (Cloud Function / API middleware) — client-side checks are UX guardrails only and can
 * be bypassed on rooted devices.
 *
 * Product IDs must match exactly what is configured in Google Play Console.
 */
object SubscriptionTierLimits {
    private const val PRODUCT_BASIC = "premium_basic"
    private const val PRODUCT_PRO   = "premium_pro"
    private const val PRODUCT_ULTRA = "premium_ultra"

    val FREE  = TierLimits(songsPerMonth = 5,           podcastEpsPerMonth = 2,           realtimeMinutesPerMonth = 10)
    val BASIC = TierLimits(songsPerMonth = 30,          podcastEpsPerMonth = 10,          realtimeMinutesPerMonth = 60)
    val PRO   = TierLimits(songsPerMonth = 100,         podcastEpsPerMonth = 30,          realtimeMinutesPerMonth = 150)
    val ULTRA = TierLimits(songsPerMonth = Int.MAX_VALUE, podcastEpsPerMonth = Int.MAX_VALUE, realtimeMinutesPerMonth = Int.MAX_VALUE)

    /**
     * Returns the limits for a given Play product ID.
     * Returns [FREE] when [productId] is null (not subscribed) or unrecognised.
     */
    fun forProductId(productId: String?): TierLimits = when (productId) {
        PRODUCT_BASIC -> BASIC
        PRODUCT_PRO   -> PRO
        PRODUCT_ULTRA -> ULTRA
        else          -> FREE
    }

    /**
     * Returns a human-readable display name for a product ID.
     * Used in [UsageLimitBottomSheet] messaging.
     */
    fun displayNameFor(productId: String?): String = when (productId) {
        PRODUCT_BASIC -> "Basic Creator"
        PRODUCT_PRO   -> "Pro Studio"
        PRODUCT_ULTRA -> "Ultra Unlimited"
        else          -> "Free"
    }

    /**
     * Returns the next tier's product ID above [currentProductId], or null if already at Ultra.
     * Used to pre-select the recommended plan on the upgrade screen.
     */
    fun nextTierProductId(currentProductId: String?): String? = when (currentProductId) {
        null          -> PRODUCT_BASIC
        PRODUCT_BASIC -> PRODUCT_PRO
        PRODUCT_PRO   -> PRODUCT_ULTRA
        else          -> null   // already at Ultra — no upgrade available
    }
}
