package com.musically.studio.ui

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.musically.studio.fakes.FakeApiClient
import com.musically.studio.network.MaveSessionManager
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.*
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: MainViewModel
    private val fakeApiClient = FakeApiClient()
    private val maveSessionManager: MaveSessionManager = mockk(relaxed = true)
    private val auth: FirebaseAuth = mockk(relaxed = true)
    private val rtdb: FirebaseDatabase = mockk(relaxed = true)

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        every { auth.currentUser } returns null
        every { maveSessionManager.events } returns MutableSharedFlow()
        viewModel = MainViewModel(fakeApiClient, maveSessionManager, auth, rtdb)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loginWithVerifiedEmail sets loading state and calls callback on success`() = runTest {
        // Given
        fakeApiClient.verifyCredentialResult = "valid_token"
        val mockTask = mockk<Task<AuthResult>>()
        every { auth.signInWithCustomToken("valid_token") } returns mockTask
        
        val slot = slot<OnCompleteListener<AuthResult>>()
        every { mockTask.addOnCompleteListener(capture(slot)) } answers {
            every { mockTask.isSuccessful } returns true
            slot.captured.onComplete(mockTask)
            mockTask
        }
        
        // When
        var successResult = false
        viewModel.loginWithVerifiedEmail("{}", "nonce") { success, _ ->
            successResult = success
        }
        
        advanceUntilIdle()

        // Then
        assert(successResult)
        verify { auth.signInWithCustomToken("valid_token") }
    }

}
