package com.musically.studio.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.musically.studio.ui.MainViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.every
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel: MainViewModel = mockk(relaxed = true)

    @Test
    fun `clicking Generate Music triggers text command`() {
        every { viewModel.thinkingText } returns MutableStateFlow("")
        every { viewModel.isRecording } returns MutableStateFlow(false)
        every { viewModel.isHapticFeedbackEnabled } returns MutableStateFlow(true)
        every { viewModel.messages } returns mutableStateListOf()
        every { viewModel.isMusicAccountConnected } returns MutableStateFlow(true)

        composeTestRule.setContent {
            HomeScreen(
                viewModel = viewModel,
                isWearableConnected = true,
                onNavigateToDevices = {}
            )
        }

        composeTestRule.onNodeWithText("Generate Music").performClick()
        verify { viewModel.sendTextCommand("Generate Music") }
    }
}
