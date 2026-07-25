package com.musically.studio.appfunctions

import android.content.Context
import android.content.Intent
import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.AppFunction
import com.musically.studio.MainActivity
import com.musically.studio.WearableStreamingService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Exposes Mave Studio's core AI capabilities to system-level agents.
 */
@Singleton
class MaveFunctions @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) {

    private fun launchMainActivity(destination: String? = null, prompt: String? = null) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (destination != null) putExtra("DESTINATION", destination)
            if (prompt != null) putExtra("PROMPT", prompt)
        }
        applicationContext.startActivity(intent)
    }

    /**
     * Generate music based on a natural language vibe description.
     *
     * @param context The execution context.
     * @param text Musical atmosphere description to generate.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun strikeVibe(context: AppFunctionContext, text: String) {
        launchMainActivity(prompt = text)
    }

    /**
     * Start real-time AI narration of the user's environment.
     * Required workflow: Call this to initiate a voice and camera streaming session.
     *
     * @param context The execution context.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun narratePOV(context: AppFunctionContext) {
        val intent = Intent(applicationContext, WearableStreamingService::class.java)
        applicationContext.startForegroundService(intent)
        WearableStreamingService.startVoiceRecording()
    }

    /**
     * Search for a track or podcast by query string.
     *
     * @param context The execution context.
     * @param query Search string for track or podcast name.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun searchForContent(context: AppFunctionContext, query: String) {
        launchMainActivity(destination = "search", prompt = "Search for $query")
    }

    /**
     * Navigate to the library of saved tracks and podcasts.
     *
     * @param context The execution context.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun openLibrary(context: AppFunctionContext) {
        launchMainActivity(destination = "library")
    }
    
    /**
     * Navigate to the home screen showing community and user tracks.
     *
     * @param context The execution context.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun openHome(context: AppFunctionContext) {
        launchMainActivity(destination = "home")
    }
}
