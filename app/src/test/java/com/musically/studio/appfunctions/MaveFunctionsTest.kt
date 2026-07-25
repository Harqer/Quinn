package com.musically.studio.appfunctions

import android.content.Context
import android.content.Intent
import androidx.appfunctions.AppFunctionContext
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.ArgumentMatchers.any
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MaveFunctionsTest {

    @Mock
    private lateinit var applicationContext: Context

    @Mock
    private lateinit var appFunctionContext: AppFunctionContext

    private lateinit var maveFunctions: MaveFunctions

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        maveFunctions = MaveFunctions(applicationContext)
    }

    @Test
    fun testStrikeVibe() = runTest {
        maveFunctions.strikeVibe(appFunctionContext, "Chill lofi beats")
        verify(applicationContext).startActivity(any(Intent::class.java))
    }

    @Test
    fun testGeneratePodcast() = runTest {
        maveFunctions.generatePodcast(appFunctionContext, "AI history")
        verify(applicationContext).startActivity(any(Intent::class.java))
    }

    @Test
    fun testSearchForContent() = runTest {
        maveFunctions.searchForContent(appFunctionContext, "Podcasts about AI")
        verify(applicationContext).startActivity(any(Intent::class.java))
    }

    @Test
    fun testOpenLibrary() = runTest {
        maveFunctions.openLibrary(appFunctionContext)
        verify(applicationContext).startActivity(any(Intent::class.java))
    }

    @Test
    fun testOpenHome() = runTest {
        maveFunctions.openHome(appFunctionContext)
        verify(applicationContext).startActivity(any(Intent::class.java))
    }
}
