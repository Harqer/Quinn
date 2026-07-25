package com.musically.studio.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = RobolectricDeviceQualifiers.Pixel5)
class MediaCoverCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @org.junit.Ignore("Failing with Robolectric configuration on CI")
    @Test
    fun captureMediaCoverCard() {
        composeTestRule.setContent {
            MediaCoverCard(
                title = "Test Album",
                subtitle = "Test Artist",
                imageUrl = "https://example.com/image.png"
            )
        }

        // Capture a screenshot using Roborazzi
        composeTestRule.onRoot().captureRoboImage("build/outputs/roborazzi/MediaCoverCard.png")
    }
}
