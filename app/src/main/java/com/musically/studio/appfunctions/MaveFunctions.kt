package com.musically.studio.appfunctions

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.AppFunction
import com.musically.studio.network.MaveSessionManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Exposes Mave Studio's core AI capabilities to system-level agents.
 */
@Singleton
class MaveFunctions @Inject constructor(
    private val sessionManager: MaveSessionManager
) {
    /**
     * Initiates music generation based on a natural language vibe description.
     *
     * @param context The execution context.
     * @param text The natural language description of the desired musical atmosphere.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun strikeVibe(context: AppFunctionContext, text: String) {
        sessionManager.connect()
        sessionManager.sendEvent("feedback", mapOf("text" to text))
    }

    /**
     * Dynamically warps the current musical session's parameters.
     *
     * @param context The execution context.
     * @param bpm The desired beats per minute. If null, the current BPM is maintained.
     * @param density The musical density (0.0 to 1.0). If null, current density is maintained.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun warpMusic(context: AppFunctionContext, bpm: Int?, density: Float?) {
        sessionManager.connect()
        val params = mutableMapOf<String, Any>()
        bpm?.let { params["bpm"] = it }
        density?.let { params["density"] = it }
        sessionManager.sendEvent("steering_action", mapOf("params" to params))
    }

    /**
     * Triggers real-time AI narration of the user's current environment.
     *
     * @param context The execution context.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun narratePOV(context: AppFunctionContext) {
        sessionManager.connect()
        sessionManager.sendEvent("text_command", mapOf("text" to "Narrate my surroundings"))
    }
}
