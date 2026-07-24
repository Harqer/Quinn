package com.musically.studio.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object FrameProcessor {
    /**
     * Throttles a flow of frames to a maximum frequency (e.g. 1 frame per second).
     * This is cost-effective and sufficient for AI scene analysis.
     */
    fun <T> throttleFrames(frames: Flow<T>, periodMs: Long = 1000L): Flow<T> = flow {
        var lastEmittedTime = 0L
        frames.collect { frame ->
            val now = System.currentTimeMillis()
            if (now - lastEmittedTime >= periodMs) {
                emit(frame)
                lastEmittedTime = now
            }
        }
    }
}
