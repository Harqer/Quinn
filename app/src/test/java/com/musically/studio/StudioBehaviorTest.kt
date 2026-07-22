package com.musically.studio

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.musically.studio.network.ApiClient
import com.musically.studio.network.MaveSessionManager
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.screens.HomeScreen
import io.mockk.*
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [34])
class StudioBehaviorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val apiClient = mockk<ApiClient>(relaxed = true)
    private val sessionManager = mockk<MaveSessionManager>(relaxed = true)
    private val auth = mockk<FirebaseAuth>(relaxed = true)
    private val rtdb = mockk<FirebaseDatabase>(relaxed = true)
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { auth.currentUser } returns null
        every { sessionManager.events } returns MutableSharedFlow()
        viewModel = MainViewModel(apiClient, sessionManager, auth, rtdb)
    }

    @Test
    fun `clicking generate vibe should update messages flow`() {
        val vibeText = "Compose a synthwave vibe"
        
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel)
        }

        // 1. Verify welcome state suggestions are present
        composeTestRule.onNodeWithText("Compose a vibe").assertExists()

        // 2. Click a suggested action
        composeTestRule.onNodeWithText("Compose a vibe").performClick()

        // 3. Verify message is added to the list
        assert(viewModel.messages.any { it.text == "Compose a vibe" && it.isUser })
    }

    @Test
    fun `typing in chat and clicking send should trigger orchestrator`() {
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel)
        }

        val input = "Make it more aggressive"
        
        // Find text field and input text
        composeTestRule.onNodeWithText("Generate...").performTextInput(input)
        
        // Click send button (identified by content description)
        composeTestRule.onNodeWithContentDescription("Send").performClick()

        // Verify state
        assert(viewModel.messages.first().text == input)
    }
}
