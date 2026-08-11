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
 * 
 * @param success True if the operation was successful.
 * @param message A descriptive message about the result.
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class PlaybackResult(
    val success: Boolean,
    val message: String
)

/**
 * Represents a content item like a podcast or episode.
 * 
 * @param id The unique identifier.
 * @param title The title of the content.
 * @param type The type of content (e.g., PODCAST, EPISODE).
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class ContentItem(
    val id: String,
    val title: String,
    val type: String
)

/**
 * Represents the result of a search operation.
 * 
 * @param items The matching content items.
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class SearchResult(
    val items: List<ContentItem>
)

@RequiresApi(36)
@AndroidEntryPoint
@AppFunctionServiceEntryPoint(serviceName = "MaveAppFunctionService", appFunctionXmlFileName = "mave_app_function_service")
abstract class AbstractMaveAppFunctionService : AppFunctionService() {

    @Inject internal lateinit var dataConnectRepository: DataConnectRepository

    /**
     * Search for podcasts, episodes, or audiobooks by a text query.
     * Call this before 'playPodcast' or 'playEpisode' to obtain valid IDs.
     * 
     * @param query The text to search for.
     * @param contentType The type of content to search for, such as "PODCAST", "EPISODE", "AUDIOBOOK", or "ALL".
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun searchContent(query: String, contentType: String): SearchResult = withContext(Dispatchers.IO) {
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
     * Finds the show's latest episode by showId and plays it.
     * 
     * @param showId The ID of the podcast to play.
     */
    @AppFunction(isDescribedByKDoc = true)
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
     * Call 'searchContent' first to obtain a valid episodeId.
     * 
     * @param episodeId The ID of the episode to play.
     */
    @AppFunction(isDescribedByKDoc = true)
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
     * Triggers AI song generation from a text prompt and starts playback when generation completes.
     * This operation may take up to 30 seconds.
     * 
     * @param prompt The text prompt describing the song to generate.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun playAiSong(prompt: String): PlaybackResult = withContext(Dispatchers.IO) {
        startPlaybackService("PLAY_AI_SONG", prompt)
        PlaybackResult(true, "Started playing AI generated song for: $prompt")
    }

    /**
     * Adds an episode to the current playback queue without interrupting current track.
     * 
     * @param episodeId The ID of the episode to add to the queue.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun addToQueue(episodeId: String): PlaybackResult = withContext(Dispatchers.IO) {
        startPlaybackService("ADD_TO_QUEUE", episodeId)
        PlaybackResult(true, "Added episode $episodeId to queue")
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
}
