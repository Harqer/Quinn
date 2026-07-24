package com.musically.studio.appfunctions

import androidx.appfunctions.AppFunctionContext
import com.musically.studio.network.MaveSessionManager
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MaveFunctionsTest {

    @Mock
    private lateinit var sessionManager: MaveSessionManager

    @Mock
    private lateinit var appFunctionContext: AppFunctionContext

    private lateinit var maveFunctions: MaveFunctions

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        maveFunctions = MaveFunctions(sessionManager)
    }

    @Test
    fun testStrikeVibe() = runTest {
        maveFunctions.strikeVibe(appFunctionContext, "Chill lofi beats")
        verify(sessionManager).connect()
        verify(sessionManager).sendEvent("feedback", mapOf("text" to "Chill lofi beats"))
    }

    @Test
    fun testWarpMusic() = runTest {
        maveFunctions.warpMusic(appFunctionContext, 120, 0.8f)
        verify(sessionManager).connect()
        verify(sessionManager).sendEvent(
            "steering_action",
            mapOf("params" to mapOf("bpm" to 120, "density" to 0.8f))
        )
    }
    
    @Test
    fun testWarpMusic_PartialParams() = runTest {
        maveFunctions.warpMusic(appFunctionContext, null, 0.5f)
        verify(sessionManager).connect()
        verify(sessionManager).sendEvent(
            "steering_action",
            mapOf("params" to mapOf("density" to 0.5f))
        )
    }

    @Test
    fun testNarratePOV() = runTest {
        maveFunctions.narratePOV(appFunctionContext)
        verify(sessionManager).connect()
        verify(sessionManager).sendEvent("text_command", mapOf("text" to "Narrate my surroundings"))
    }

    @Test
    fun testSearchForContent() = runTest {
        maveFunctions.searchForContent(appFunctionContext, "Podcasts about AI")
        verify(sessionManager).connect()
        verify(sessionManager).sendEvent("text_command", mapOf("text" to "Search for Podcasts about AI"))
    }

    @Test
    fun testOpenLibrary() = runTest {
        maveFunctions.openLibrary(appFunctionContext)
        verify(sessionManager).connect()
        verify(sessionManager).sendEvent("navigation", mapOf("destination" to "library"))
    }

    @Test
    fun testOpenHome() = runTest {
        maveFunctions.openHome(appFunctionContext)
        verify(sessionManager).connect()
        verify(sessionManager).sendEvent("navigation", mapOf("destination" to "home"))
    }
}
