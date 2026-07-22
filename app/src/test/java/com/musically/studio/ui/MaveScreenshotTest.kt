package com.musically.studio.ui

import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.musically.studio.ui.theme.MaveAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class MaveScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun captureThemeBaseline() {
        composeTestRule.setContent {
            MaveAppTheme {
                Text("Mave Studio Baseline")
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/screenshots/theme_baseline.png")
    }
}
