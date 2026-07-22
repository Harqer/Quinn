package com.musically.studio.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import com.musically.studio.ui.screens.onboarding.AuthOptionsScreen
import com.musically.studio.ui.theme.MaveAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel5)
class AuthOptionsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun authOptionsScreen_rendersCorrectly() {
        composeTestRule.setContent {
            MaveAppTheme {
                AuthOptionsScreen(
                    onEmailClick = {},
                    onGoogleClick = {},
                    onAppleClick = {},
                    onLoginClick = {},
                    onBackClick = {}
                )
            }
        }
        
        composeTestRule.onRoot().captureRoboImage()
    }
}
