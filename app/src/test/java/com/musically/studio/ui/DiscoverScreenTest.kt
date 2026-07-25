package com.musically.studio.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.musically.studio.ui.screens.DiscoverScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DiscoverScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDiscoverScreen_showsCategories() {
        composeTestRule.setContent {
            DiscoverScreen(
                onNavigate = {}
            )
        }

        // Verify that standard categories are displayed
        composeTestRule.onNodeWithText("Acoustic").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pop").assertIsDisplayed()
        
        // Test interaction
        composeTestRule.onNodeWithText("Acoustic").performClick()
    }
}
