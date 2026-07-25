package com.musically.studio.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.musically.studio.network.fakes.FakeApiClient
import com.musically.studio.network.fakes.FakeMaveSessionManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import android.content.SharedPreferences

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var fakeApiClient: FakeApiClient
    private lateinit var fakeMaveSessionManager: FakeMaveSessionManager
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        val context = ApplicationProvider.getApplicationContext<Context>()
        
        // Mock Firebase Auth
        val mockAuth = mockk<FirebaseAuth>(relaxed = true)
        val mockUser = mockk<FirebaseUser>(relaxed = true)
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "test_user_id"
        
        // Mock Firebase RTDB
        val mockDb = mockk<FirebaseDatabase>(relaxed = true)
        val mockRef = mockk<DatabaseReference>(relaxed = true)
        every { mockDb.getReference(any()) } returns mockRef
        
        fakeApiClient = FakeApiClient()
        fakeMaveSessionManager = FakeMaveSessionManager()

        viewModel = MainViewModel(
            context = context,
            apiClient = fakeApiClient,
            maveSessionManager = fakeMaveSessionManager,
            auth = mockAuth,
            rtdb = mockDb
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test togglePlayPause updates playing state`() = runTest {
        // Initial state
        assertEquals(false, viewModel.isPlaying.value)
        
        viewModel.togglePlayPause()
        
        assertEquals(true, viewModel.isPlaying.value)
        assertEquals("play", fakeMaveSessionManager.playbackState)
        
        viewModel.togglePlayPause()
        
        assertEquals(false, viewModel.isPlaying.value)
        assertEquals("pause", fakeMaveSessionManager.playbackState)
    }

    @Test
    fun `test toggleShuffle updates shuffle state`() = runTest {
        assertEquals(false, viewModel.isShuffleEnabled.value)
        
        viewModel.toggleShuffle()
        
        assertEquals(true, viewModel.isShuffleEnabled.value)
        
        val event = fakeMaveSessionManager.sentEvents.last()
        assertEquals("toggle_shuffle", event.first)
        assertEquals(true, event.second["enabled"])
    }
}
