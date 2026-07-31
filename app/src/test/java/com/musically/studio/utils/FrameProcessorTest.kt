package com.musically.studio.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FrameProcessorTest {

    @Test
    fun `throttleFrames should only emit one frame within 1 second period`() = runTest {
        val frames = listOf(1, 2, 3).asFlow()
        // In this test, all frames are emitted immediately. 
        // Throttle should only allow the first one and block the others.
        val result = FrameProcessor.throttleFrames(frames, periodMs = 1000L).toList()
        
        assertEquals(1, result.size)
        assertEquals(1, result[0])
    }
}
