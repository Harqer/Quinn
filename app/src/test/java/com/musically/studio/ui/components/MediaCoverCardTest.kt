package com.musically.studio.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import com.musically.studio.ui.theme.MaveAppTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel5)
class MediaCoverCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mediaCoverCard_clickPlay_callsOnPlayClick() {
        var playClicked = false

        composeTestRule.setContent {
            MaveAppTheme {
                MediaCoverCard(
                    title = "Test Track",
                    subtitle = "Test Artist",
                    imageUrl = null,
                    onPlayClick = { playClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Play").performClick()

        assertTrue("Play button click callback was not invoked", playClicked)
    }

    @Test
    fun mediaCoverCard_clickLike_callsOnLikeClick() {
        var likeClicked = false

        composeTestRule.setContent {
            MaveAppTheme {
                MediaCoverCard(
                    title = "Test Track",
                    subtitle = "Test Artist",
                    imageUrl = null,
                    isLiked = false,
                    onLikeClick = { likeClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Like").performClick()

        assertTrue("Like button click callback was not invoked", likeClicked)
    }
    
    @Test
    fun mediaCoverCard_clickShare_callsOnShareClick() {
        var shareClicked = false

        composeTestRule.setContent {
            MaveAppTheme {
                MediaCoverCard(
                    title = "Test Track",
                    subtitle = "Test Artist",
                    imageUrl = null,
                    onShareClick = { shareClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Share").performClick()

        assertTrue("Share button click callback was not invoked", shareClicked)
    }

    @Test
    fun mediaCoverCard_clickMoreOptions_callsOnMoreClick() {
        var moreOptionsClicked = false

        composeTestRule.setContent {
            MaveAppTheme {
                MediaCoverCard(
                    title = "Test Track",
                    subtitle = "Test Artist",
                    imageUrl = null,
                    onMoreClick = { moreOptionsClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("More Options").performClick()

        assertTrue("More Options button click callback was not invoked", moreOptionsClicked)
    }

    @Test
    fun mediaCoverCard_layout_screenshotTest() {
        composeTestRule.setContent {
            MaveAppTheme {
                MediaCoverCard(
                    title = "Test Track",
                    subtitle = "Test Artist",
                    imageUrl = null
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage()
    }
}
