/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for EpisodeStore.kt
 */

package com.musically.studio.ui.jetcaster.core.data.repository

import com.musically.studio.data.repository.DataConnectRepository
import com.musically.studio.ui.jetcaster.core.model.EpisodeInfo
import com.musically.studio.ui.jetcaster.core.model.PodcastInfo
import com.musically.studio.ui.jetcaster.core.model.PodcastToEpisodeInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.OffsetDateTime
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

interface EpisodeStore {
    fun episodeAndPodcastWithUri(episodeUri: String): Flow<PodcastToEpisodeInfo>
}

@Singleton
class RealEpisodeStore @Inject constructor(
    private val dataConnectRepository: DataConnectRepository
) : EpisodeStore {
    override fun episodeAndPodcastWithUri(episodeUri: String): Flow<PodcastToEpisodeInfo> = flow {
        val episodeData = dataConnectRepository.getEpisode(episodeUri)
        if (episodeData != null) {
            val epInfo = EpisodeInfo(
                uri = episodeData.id,
                title = episodeData.title,
                summary = episodeData.description ?: "",
                duration = Duration.ofMillis(episodeData.durationMs.toLong()),
                published = OffsetDateTime.now(),
                mediaUrls = listOfNotNull(episodeData.audioUrl)
            )
            val podcastInfo = PodcastInfo(
                uri = episodeData.show.id,
                title = episodeData.show.title,
                author = "Lyria Audio Studio",
                imageUrl = episodeData.show.coverUrl ?: ""
            )
            emit(PodcastToEpisodeInfo(episode = epInfo, podcast = podcastInfo))
        } else {
            // Fallback for episode playback by direct audio URI
            val epInfo = EpisodeInfo(
                uri = episodeUri,
                title = "Episode Stream",
                summary = "Live Episode Audio Stream",
                duration = Duration.ofMinutes(15),
                published = OffsetDateTime.now(),
                mediaUrls = listOf(episodeUri)
            )
            val podcastInfo = PodcastInfo(
                uri = "live_stream",
                title = "Lyria Stream",
                author = "Lyria",
                imageUrl = ""
            )
            emit(PodcastToEpisodeInfo(episode = epInfo, podcast = podcastInfo))
        }
    }
}
