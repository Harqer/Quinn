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
     * Initiate music generation based on a natural language vibe description.
     *
     * @param context The execution context.
     * @param text Musical atmosphere description.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun strikeVibe(context: AppFunctionContext, text: String) {
        sessionManager.connect()
        sessionManager.sendEvent("feedback", mapOf("text" to text))
    }

    /**
     * Warp the current musical session's parameters.
     * Required workflow: Call "strikeVibe" first to initiate a session.
     *
     * @param context The execution context.
     * @param bpm Beats per minute. If null, current BPM is maintained.
     * @param density Musical density (0.0 to 1.0). If null, current density is maintained.
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
     * Trigger real-time AI narration of the user's environment.
     *
     * @param context The execution context.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun narratePOV(context: AppFunctionContext) {
        sessionManager.connect()
        sessionManager.sendEvent("text_command", mapOf("text" to "Narrate my surroundings"))
    }

    /**
     * Search for a track or podcast by query.
     *
     * @param context The execution context.
     * @param query Search string.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun searchForContent(context: AppFunctionContext, query: String) {
        sessionManager.connect()
        sessionManager.sendEvent("text_command", mapOf("text" to "Search for $query"))
    }

    /**
     * Navigate to the library of saved tracks and podcasts.
     *
     * @param context The execution context.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun openLibrary(context: AppFunctionContext) {
        sessionManager.connect()
        sessionManager.sendEvent("navigation", mapOf("destination" to "library"))
    }
    
    /**
     * Navigate to the home screen showing community and user tracks.
     *
     * @param context The execution context.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun openHome(context: AppFunctionContext) {
        sessionManager.connect()
        sessionManager.sendEvent("navigation", mapOf("destination" to "home"))
    }
}
