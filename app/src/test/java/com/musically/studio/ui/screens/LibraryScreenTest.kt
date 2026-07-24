package com.musically.studio.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import android.content.Context
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel5)
class LibraryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        val mockAuth = Mockito.mock(FirebaseAuth::class.java)
        val mockDb = Mockito.mock(FirebaseDatabase::class.java)
        val mockSession = Mockito.mock(MaveSessionManager::class.java)
        val mockFlow = kotlinx.coroutines.flow.MutableSharedFlow<String>()
        Mockito.doReturn(mockFlow).`when`(mockSession).events
        viewModel = MainViewModel(ApplicationProvider.getApplicationContext(), FakeApiClient(), mockSession, mockAuth, mockDb)
    }

    @Test
    fun libraryScreen_rendersCorrectly() {
        composeTestRule.setContent {
            MaveAppTheme {
                LibraryScreen(
                    viewModel = viewModel,
                    onNavigateToNowPlaying = {},
                    onNavigateToAlbum = {},
                    onNavigateToHome = {},
                    onNavigateToPlaylist = {}
                )
            }
        }
        
        composeTestRule.onRoot().captureRoboImage()
    }
}
