package com.musically.studio.ui.screens

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.musically.studio.network.FakeApiClient
import com.musically.studio.network.MaveSessionManager
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.theme.MaveAppTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.verify
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel5)
class StudioHomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: MainViewModel
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        val mockAuth = Mockito.mock(FirebaseAuth::class.java)
        val mockDb = Mockito.mock(FirebaseDatabase::class.java)
        val mockSession = Mockito.mock(MaveSessionManager::class.java)
        val mockFlow = MutableSharedFlow<String>()
        Mockito.doReturn(mockFlow).`when`(mockSession).events
        mockContext = ApplicationProvider.getApplicationContext()
        
        val realViewModel = MainViewModel(mockContext, FakeApiClient(), mockSession, mockAuth, mockDb)
        viewModel = Mockito.spy(realViewModel)
    }

    @Test
    fun studioHomeScreen_voiceInputButton_callsRecordVoice() {
        composeTestRule.setContent {
            MaveAppTheme {
                StudioHomeScreen(
                    viewModel = viewModel,
                    onNavigateToSettings = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Voice Input").performClick()
        
        verify(viewModel).recordVoice(org.mockito.kotlin.any())
    }

    @Test
    fun studioHomeScreen_sendTextCommand_callsSendTextCommand() {
        composeTestRule.setContent {
            MaveAppTheme {
                StudioHomeScreen(
                    viewModel = viewModel,
                    onNavigateToSettings = {}
                )
            }
        }

        val testPrompt = "Generate an upbeat electronic track"
        
        composeTestRule.onNode(androidx.compose.ui.test.hasSetTextAction()).performTextInput(testPrompt)

        composeTestRule.onNodeWithContentDescription("Send").performClick()

        verify(viewModel).sendTextCommand(testPrompt)
    }
    
    @Test
    fun studioHomeScreen_layout_screenshotTest() {
        composeTestRule.setContent {
            MaveAppTheme {
                StudioHomeScreen(
                    viewModel = viewModel,
                    onNavigateToSettings = {}
                )
            }
        }
        
        composeTestRule.onRoot().captureRoboImage()
    }
}
