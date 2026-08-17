package com.musically.studio.appfunctions

import android.content.ComponentName
import android.content.Intent
import androidx.annotation.RequiresApi
import androidx.appfunctions.AppFunction
import androidx.appfunctions.AppFunctionService
import androidx.appfunctions.AppFunctionServiceEntryPoint
import androidx.appfunctions.AppFunctionSerializable
import com.musically.studio.data.repository.DataConnectRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * Represents the result of a playback operation.
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class PlaybackResult(
    /** True if the operation was successful. */
    val success: Boolean,
    /** A descriptive message about the result. */
    val message: String
)

/**
 * Represents a content item like a podcast or episode.
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class ContentItem(
    /** The unique identifier. */
    val id: String,
    /** The title of the content. */
    val title: String,
    /** The type of content (e.g., PODCAST, EPISODE). */
    val type: String
)

/**
 * Represents the result of a search operation.
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class SearchResult(
    /** The matching content items. */
    val items: List<ContentItem>
)

@RequiresApi(36)
@AndroidEntryPoint
@AppFunctionServiceEntryPoint(serviceName = "MaveAppFunctionService", appFunctionXmlFileName = "mave_app_function_service")
abstract class AbstractMaveAppFunctionService : AppFunctionService() {

    @Inject internal lateinit var dataConnectRepository: DataConnectRepository

    /**
     * Search for podcasts, episodes, or audiobooks by a text query.
     * Required workflow: Call this before "playPodcast" or "playEpisode" to obtain valid IDs.
     * 
     * @param query The text to search for.
     * @param contentType The type of content to search for, such as "PODCAST", "EPISODE", "AUDIOBOOK", or "ALL".
     */
    @AppFunction
    suspend fun searchContent(
        query: String,
        contentType: String
    ): SearchResult = withContext(Dispatchers.IO) {
        val results = mutableListOf<ContentItem>()
        if (contentType == "PODCAST" || contentType == "ALL") {
            val podcasts = dataConnectRepository.getPodcasts().first()
            results.addAll(
                podcasts.filter { it.title?.contains(query, ignoreCase = true) == true }
                    .take(5)
                    .map { ContentItem(id = it.id, title = it.title ?: "", type = "PODCAST") }
            )
        }
        if (contentType == "AUDIOBOOK" || contentType == "ALL") {
            val audiobooks = dataConnectRepository.getAudiobooks().first()
            results.addAll(
                audiobooks.filter { it.title?.contains(query, ignoreCase = true) == true }
                    .take(5)
                    .map { ContentItem(id = it.id, title = it.title ?: "", type = "AUDIOBOOK") }
            )
        }
        if (contentType == "EPISODE" || contentType == "ALL") {
            val tracks = dataConnectRepository.searchTracks(query).first()
            results.addAll(
                tracks.take(5)
                    .map { ContentItem(id = it.id, title = it.title ?: "", type = "EPISODE") }
            )
        }
        SearchResult(results.take(5))
    }

    /**
     * Play a specific podcast by its show ID.
     * Required workflow: Call "searchContent" first to obtain a valid showId.
     * Finds the show's latest episode by showId and plays it.
     * 
     * @param showId The ID of the podcast to play.
     */
    @AppFunction
    suspend fun playPodcast(showId: String): PlaybackResult = withContext(Dispatchers.IO) {
        val podcasts = dataConnectRepository.getPodcasts().first()
        val show = podcasts.find { it.id == showId }
        if (show != null) {
            startPlaybackService("PLAY_PODCAST", showId)
            PlaybackResult(true, "Playing podcast: ${show.title}")
        } else {
            PlaybackResult(false, "Podcast not found")
        }
    }

    /**
     * Play a specific episode by its episode ID.
     * Required workflow: Call "searchContent" first to obtain a valid episodeId.
     * 
     * @param episodeId The ID of the episode to play.
     */
    @AppFunction
    suspend fun playEpisode(episodeId: String): PlaybackResult = withContext(Dispatchers.IO) {
        val track = dataConnectRepository.getTrack(episodeId)
        if (track != null) {
            startPlaybackService("PLAY_EPISODE", episodeId)
            PlaybackResult(true, "Playing episode: ${track.title}")
        } else {
            PlaybackResult(false, "Episode not found")
        }
    }

    /**
     * Generate an AI song from a text prompt and start playback.
     * This operation may take up to 30 seconds.
     * 
     * @param prompt The text prompt describing the song to generate.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun playAiSong(prompt: String): PlaybackResult = withContext(Dispatchers.IO) {
        val intent = Intent(this@AbstractMaveAppFunctionService, com.musically.studio.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("PROMPT", prompt)
        }
        try {
            startActivity(intent)
            PlaybackResult(true, "Started generating AI song for: $prompt")
        } catch (e: Exception) {
            PlaybackResult(false, "Failed to start AI song generation: ${e.message}")
        }
    }

    /**
     * Start a hands-free interactive voice session with the Genkit AI agent.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun startLiveSession(): PlaybackResult = withContext(Dispatchers.IO) {
        val intent = Intent(this@AbstractMaveAppFunctionService, com.musically.studio.WearableStreamingService::class.java)
        try {
            startForegroundService(intent)
            com.musically.studio.WearableStreamingService.startVoiceRecording()
            PlaybackResult(true, "Live session started")
        } catch (e: Exception) {
            PlaybackResult(false, "Failed to start live session: ${e.message}")
        }
    }

    private fun startPlaybackService(actionName: String, extraData: String) {
        val intent = Intent("androidx.media3.session.MediaSessionService").apply {
            component = ComponentName(this@AbstractMaveAppFunctionService, "com.musically.studio.audio.PlaybackService")
            putExtra("action", actionName)
            putExtra("data", extraData)
        }
        try {
            startService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun launchMainActivity(destination: String? = null, prompt: String? = null): PlaybackResult = withContext(Dispatchers.IO) {
        val intent = Intent(this@AbstractMaveAppFunctionService, com.musically.studio.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (destination != null) putExtra("DESTINATION", destination)
            if (prompt != null) putExtra("PROMPT", prompt)
        }
        try {
            startActivity(intent)
            PlaybackResult(true, "Successfully executed action.")
        } catch (e: Exception) {
            PlaybackResult(false, "Failed to execute action: ${e.message}")
        }
    }

    /**
     * Generate an AI podcast episode based on a topic description.
     * Required workflow: Execute this function only when the user explicitly requests to generate a new podcast.
     *
     * @param topic Topic or script for the podcast generation.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun generatePodcast(topic: String): PlaybackResult {
        return launchMainActivity(prompt = "Generate a podcast about $topic")
    }

    /**
     * Open the library of saved tracks and podcasts.
     * Required workflow: Execute this function when the user asks to see their saved content.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun openLibrary(): PlaybackResult {
        return launchMainActivity(destination = "library")
    }

    /**
     * Open the home screen showing community and user tracks.
     * Required workflow: Execute this function when the user asks to go home or see community content.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun openHome(): PlaybackResult {
        return launchMainActivity(destination = "home")
    }

    /**
     * Start a music trivia session with Lyria.
     * Required workflow: Execute this function when the user wants to play trivia.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun startTrivia(): PlaybackResult {
        return launchMainActivity(prompt = "Start a music trivia game!")
    }

    /**
     * Take a picture and create a song based on the visual vibe.
     * Required workflow: Execute this function when the user asks to turn their view into a song.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun createSongFromPicture(): PlaybackResult {
        return launchMainActivity(prompt = "Take a picture and make a song about it")
    }

    /**
     * Fine-tune or edit a song using Lyria Realtime.
     * Required workflow: Execute this function when the user wants to refine an existing song interactively.
     *
     * @param songId The ID of the song to fine-tune.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun fineTuneSong(songId: String): PlaybackResult {
        return launchMainActivity(prompt = "Let's fine tune the song with ID: $songId")
    }
}
