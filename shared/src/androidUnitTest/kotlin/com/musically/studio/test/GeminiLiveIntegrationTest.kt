package com.musically.studio.test

import com.musically.studio.network.GeminiLiveManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.json.JSONObject

import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
@OptIn(ExperimentalCoroutinesApi::class)
class GeminiLiveIntegrationTest {

    @Test
    fun testGeminiLiveManagerFormattingAndParsing() = runTest {
        val client = OkHttpClient()
        val mockSocket = object : okhttp3.WebSocket {
            var sentMessage: String? = null
            override fun request() = okhttp3.Request.Builder().url("http://localhost").build()
            override fun queueSize() = 0L
            override fun send(text: String): Boolean {
                sentMessage = text
                return true
            }
            override fun send(bytes: okio.ByteString) = true
            override fun close(code: Int, reason: String?) = true
            override fun cancel() {}
        }
        
        val manager = GeminiLiveManager(client)
        val socketField = GeminiLiveManager::class.java.getDeclaredField("webSocket")
        socketField.isAccessible = true
        socketField.set(manager, mockSocket)
        
        // 1. Test sending a frame (formatting JSON)
        manager.sendVideoFrame("base64_data", "image/jpeg")
        assert(mockSocket.sentMessage != null)
        println("Sent message: ${mockSocket.sentMessage}")
        val sentJson = JSONObject(mockSocket.sentMessage!!)
        
        assertTrue(sentJson.has("realtimeInput"))
        val inputVideo = sentJson.getJSONObject("realtimeInput").getJSONObject("video")
        assertEquals("image/jpeg", inputVideo.getString("mimeType"))
        assertEquals("base64_data", inputVideo.getString("data"))

        // 2. Test receiving a server message (parsing JSON)
        val serverResponse = """
            {
              "serverContent": {
                "modelTurn": {
                  "parts": [
                    { "thought": "I need to respond to the user" }
                  ]
                },
                "outputTranscription": {
                  "text": "Hello, this is Gemini!"
                }
              }
            }
        """.trimIndent()
        
        var receivedTranscript: String? = null
        val job1 = launch(UnconfinedTestDispatcher(testScheduler)) {
            manager.transcripts.collect { transcript ->
                receivedTranscript = transcript
            }
        }
        
        var receivedThought: String? = null
        val job2 = launch(UnconfinedTestDispatcher(testScheduler)) {
            manager.thoughts.collect { thought ->
                receivedThought = thought
            }
        }

        // 3. Process server message
        val handleMethod = GeminiLiveManager::class.java.getDeclaredMethod("handleServerMessage", String::class.java)
        handleMethod.isAccessible = true
        handleMethod.invoke(manager, serverResponse)

        // 4. Wait for flows to emit
        advanceUntilIdle()

        // 5. Verify the transcript and thought
        assertEquals("Hello, this is Gemini!", receivedTranscript)
        assertEquals("I need to respond to the user", receivedThought)
        
        job1.cancel()
        job2.cancel()
    }
}
