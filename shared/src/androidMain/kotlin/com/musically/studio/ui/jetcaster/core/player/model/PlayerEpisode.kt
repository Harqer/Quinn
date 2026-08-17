/**
 * @AtomicLevel: Atom
 * @SemanticPurpose: Android Component for PlayerEpisode.kt
 */

package com.musically.studio.ui.jetcaster.core.player.model

import com.musically.studio.ui.jetcaster.core.model.EpisodeInfo
import com.musically.studio.ui.jetcaster.core.model.PodcastInfo
import com.musically.studio.ui.jetcaster.core.model.PodcastToEpisodeInfo

data class PlayerEpisode(
    val podcastInfo: PodcastInfo,
    val episodeInfo: EpisodeInfo
) {
    val title: String get() = episodeInfo.title
    val podcastName: String get() = podcastInfo.title
    val duration: java.time.Duration? get() = episodeInfo.duration
    val podcastImageUrl: String get() = podcastInfo.imageUrl
    val summary: String get() = episodeInfo.summary
    val contentUrl: String get() = episodeInfo.mediaUrls.firstOrNull() ?: episodeInfo.uri
}

fun PodcastToEpisodeInfo.toPlayerEpisode(): PlayerEpisode = PlayerEpisode(
    podcastInfo = podcast,
    episodeInfo = episode,
)
