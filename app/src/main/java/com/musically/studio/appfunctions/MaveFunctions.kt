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
    @param:ApplicationContext private val applicationContext: Context
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
     * Create an AI-generated music track based on a natural language vibe description.
     *
     * @param context The execution context.
     * @param text Musical atmosphere description.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun strikeVibe(context: AppFunctionContext, text: String) {
        launchMainActivity(prompt = text)
    }

    /**
     * Generate an AI podcast episode based on a topic description.
     *
     * @param context The execution context.
     * @param topic Topic or script for the podcast generation.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun generatePodcast(context: AppFunctionContext, topic: String) {
        launchMainActivity(prompt = "Generate a podcast about $topic")
    }

    /**
     * Stream real-time AI narration of the user's camera environment.
     * Required workflow: Ask for user confirmation before starting the stream to protect privacy.
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
     * Search for a track or podcast.
     *
     * @param context The execution context.
     * @param query The search query string.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun searchForContent(context: AppFunctionContext, query: String) {
        launchMainActivity(destination = "search", prompt = "Search for $query")
    }

    /**
     * Open the library of saved tracks and podcasts.
     *
     * @param context The execution context.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun openLibrary(context: AppFunctionContext) {
        launchMainActivity(destination = "library")
    }
    
    /**
     * Open the home screen showing community and user tracks.
     *
     * @param context The execution context.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun openHome(context: AppFunctionContext) {
        launchMainActivity(destination = "home")
    }
}
