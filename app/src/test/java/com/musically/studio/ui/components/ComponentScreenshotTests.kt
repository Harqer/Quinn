package com.musically.studio.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import com.musically.studio.ui.components.atoms.MaveButton
import com.musically.studio.ui.theme.MaveAppTheme
import com.musically.studio.ui.theme.MaveStyles
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel5)
class ComponentScreenshotTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun maveButtonPrimary_rendersCorrectly() {
        composeTestRule.setContent {
            MaveAppTheme {
                MaveButton(
                    text = "Primary Button",
                    onClick = {},
                    style = MaveStyles.primaryButton,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        composeTestRule.onRoot().captureRoboImage()
    }
    
    @Test
    fun maveButtonOutline_rendersCorrectly() {
        composeTestRule.setContent {
            MaveAppTheme {
                MaveButton(
                    text = "Outline Button",
                    onClick = {},
                    style = MaveStyles.outlinedButton,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        composeTestRule.onRoot().captureRoboImage()
    }
}
