package com.musically.studio.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.musically.studio.network.FakeApiClient
import com.musically.studio.network.MaveSessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.screens.onboarding.LoginScreen
import com.musically.studio.ui.theme.MaveAppTheme
import org.junit.Before
import org.mockito.Mockito
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33])
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        val mockAuth = Mockito.mock(FirebaseAuth::class.java)
        val mockDb = Mockito.mock(FirebaseDatabase::class.java)
        val mockSession = Mockito.mock(MaveSessionManager::class.java)
        val mockGeminiLive = Mockito.mock(com.musically.studio.network.GeminiLiveManager::class.java)
        Mockito.doReturn(kotlinx.coroutines.flow.MutableSharedFlow<org.json.JSONObject>()).`when`(mockGeminiLive).functionCalls
        Mockito.doReturn(kotlinx.coroutines.flow.MutableSharedFlow<String>()).`when`(mockGeminiLive).transcripts
        Mockito.doReturn(kotlinx.coroutines.flow.MutableSharedFlow<String>()).`when`(mockGeminiLive).thoughts
        Mockito.doReturn(kotlinx.coroutines.flow.MutableStateFlow(false)).`when`(mockGeminiLive).connectionState
        val mockFlow = kotlinx.coroutines.flow.MutableSharedFlow<String>()
        Mockito.doReturn(mockFlow).`when`(mockSession).events
        viewModel = MainViewModel(ApplicationProvider.getApplicationContext(), FakeApiClient(), mockSession, mockGeminiLive, org.mockito.Mockito.mock(com.musically.studio.network.StreamingApiClient::class.java), mockAuth, mockDb)
    }

    @Test
    fun loginScreen_signUpButton_navigatesToAuthOptions() {
        var navigatedToSignUp = false
        composeTestRule.setContent {
            MaveAppTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {},
                    onNavigateToSignUp = { navigatedToSignUp = true }
                )
            }
        }

        // The exact text on the button is "Don't have an account? Sign up" or just "Sign up"
        // In LoginScreen.kt: it has TextButton with Text("Don't have an account? Sign up")
        composeTestRule.onNodeWithText("Don't have an account? Sign up").performClick()
        
        assertTrue(navigatedToSignUp)
    }
}
