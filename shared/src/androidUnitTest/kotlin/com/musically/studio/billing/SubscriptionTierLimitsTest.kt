package com.musically.studio.billing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SubscriptionTierLimitsTest {

    @Test
    fun `null or unknown product ID defaults to FREE tier limits`() {
        val nullLimits = SubscriptionTierLimits.forProductId(null)
        assertEquals(5, nullLimits.songsPerMonth)
        assertEquals(2, nullLimits.podcastEpsPerMonth)
        assertEquals(10, nullLimits.realtimeMinutesPerMonth)
        assertFalse(nullLimits.songsUnlimited)

        val unknownLimits = SubscriptionTierLimits.forProductId("unknown_product")
        assertEquals(SubscriptionTierLimits.FREE, unknownLimits)
    }

    @Test
    fun `basic tier limits are mapped correctly`() {
        val limits = SubscriptionTierLimits.forProductId("premium_basic")
        assertEquals(30, limits.songsPerMonth)
        assertEquals(10, limits.podcastEpsPerMonth)
        assertEquals(60, limits.realtimeMinutesPerMonth)
        assertFalse(limits.songsUnlimited)
    }

    @Test
    fun `pro tier limits are mapped correctly`() {
        val limits = SubscriptionTierLimits.forProductId("premium_pro")
        assertEquals(100, limits.songsPerMonth)
        assertEquals(30, limits.podcastEpsPerMonth)
        assertEquals(150, limits.realtimeMinutesPerMonth)
        assertFalse(limits.songsUnlimited)
    }

    @Test
    fun `ultra tier limits are unlimited`() {
        val limits = SubscriptionTierLimits.forProductId("premium_ultra")
        assertEquals(Int.MAX_VALUE, limits.songsPerMonth)
        assertEquals(Int.MAX_VALUE, limits.podcastEpsPerMonth)
        assertEquals(Int.MAX_VALUE, limits.realtimeMinutesPerMonth)
        assertTrue(limits.songsUnlimited)
        assertTrue(limits.podcastsUnlimited)
        assertTrue(limits.realtimeUnlimited)
    }

    @Test
    fun `displayNameFor returns correct human-readable names`() {
        assertEquals("Free", SubscriptionTierLimits.displayNameFor(null))
        assertEquals("Basic Creator", SubscriptionTierLimits.displayNameFor("premium_basic"))
        assertEquals("Pro Studio", SubscriptionTierLimits.displayNameFor("premium_pro"))
        assertEquals("Ultra Unlimited", SubscriptionTierLimits.displayNameFor("premium_ultra"))
    }

    @Test
    fun `nextTierProductId returns correct upgrade progression`() {
        assertEquals("premium_basic", SubscriptionTierLimits.nextTierProductId(null))
        assertEquals("premium_pro", SubscriptionTierLimits.nextTierProductId("premium_basic"))
        assertEquals("premium_ultra", SubscriptionTierLimits.nextTierProductId("premium_pro"))
        assertNull(SubscriptionTierLimits.nextTierProductId("premium_ultra"))
    }
}
