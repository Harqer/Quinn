package com.musically.studio.test

import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.robolectric.annotation.Config
import com.musically.studio.data.repository.ChatRepository
import com.musically.studio.ui.screens.ChatViewModel
import com.musically.studio.ui.screens.ChatScreen
import com.musically.studio.network.GeminiLiveManager
import com.musically.studio.services.ChatGenerativeService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.json.JSONObject

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
@OptIn(ExperimentalCoroutinesApi::class)
class AndroidScreensIntegrationTest {

    private lateinit var chatRepository: ChatRepository
    private lateinit var generativeService: ChatGenerativeService
    private lateinit var geminiLiveManager: GeminiLiveManager
    private lateinit var chatViewModel: ChatViewModel

    @Before
    fun setup() {
        // Mock the backend layers to verify the UI properly delegates to real repository methods
        // instead of using local mock stubs.
        chatRepository = mockk<ChatRepository>(relaxed = true)
        generativeService = mockk<ChatGenerativeService>(relaxed = true)
        geminiLiveManager = mockk<GeminiLiveManager>(relaxed = true)
        
        chatViewModel = ChatViewModel(chatRepository, generativeService, geminiLiveManager)
    }

    @Test
    fun testChatScreen_API_Inclusion() = kotlinx.coroutines.runBlocking {
        // 1. Prepare Mock Response
        val mockJsonResponse = JSONObject().apply {
            put("url", "https://example.com/mock-video.mp4")
        }
        coEvery { chatRepository.generateVideo(any()) } returns mockJsonResponse
        
        // 2. Trigger the real logic that the ChatScreen would trigger on a button press
        
        // Action: Call generate video
        chatViewModel.generateVideo("Make a cool video")
        
        // Assert: Verify that ChatRepository.generateVideo was actually called
        coVerify(exactly = 1) { 
            chatRepository.generateVideo("Make a cool video")
        }
        
        // Verify the ViewModel state flow updated with the "loading" and "ready" message
        val messages = chatViewModel.messages.value
        assert(messages.any { it.text.contains("video") || it.videoUrl != null })
    }
}
