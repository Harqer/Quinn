package com.musically.studio.engage

import com.google.android.engage.common.datamodel.ContinuationCluster
import com.google.android.engage.common.datamodel.RecommendationCluster
import com.google.android.engage.service.PublishContinuationClusterRequest
import com.google.android.engage.service.PublishRecommendationClustersRequest
import com.musically.studio.network.MavePlaylist
import com.musically.studio.network.MaveTrack
import com.musically.studio.network.MavePodcast
import com.musically.studio.network.MaveAudiobook

object ClusterRequestFactory {

    fun createContinuationClusterRequest(recentTracks: List<MaveTrack>): PublishContinuationClusterRequest {
        val continuationClusterBuilder = ContinuationCluster.Builder()
        
        recentTracks.forEach { track ->
            val trackEntity = ItemToEntityConverter.convertTrackToEntity(track)
            continuationClusterBuilder.addEntity(trackEntity)
        }

        return PublishContinuationClusterRequest.Builder()
            .setContinuationCluster(continuationClusterBuilder.build())
            .build()
    }

    fun createRecommendationClusterRequest(
        recommendedPlaylists: List<MavePlaylist>,
        recommendedPodcasts: List<MavePodcast>,
        recommendedAudiobooks: List<MaveAudiobook>
    ): PublishRecommendationClustersRequest {
        val requestBuilder = PublishRecommendationClustersRequest.Builder()
        
        val playlistClusterBuilder = RecommendationCluster.Builder()
            .setTitle("Recommended Playlists")
        recommendedPlaylists.forEach { playlist ->
            val playlistEntity = ItemToEntityConverter.convertPlaylistToEntity(playlist)
            playlistClusterBuilder.addEntity(playlistEntity)
        }
        requestBuilder.addRecommendationCluster(playlistClusterBuilder.build())

        if (recommendedPodcasts.isNotEmpty()) {
            val podcastClusterBuilder = RecommendationCluster.Builder()
                .setTitle("Recommended Podcasts")
            recommendedPodcasts.forEach { podcast ->
                val entity = ItemToEntityConverter.convertPodcastToEntity(podcast)
                podcastClusterBuilder.addEntity(entity)
            }
            requestBuilder.addRecommendationCluster(podcastClusterBuilder.build())
        }

        if (recommendedAudiobooks.isNotEmpty()) {
            val audiobookClusterBuilder = RecommendationCluster.Builder()
                .setTitle("Recommended Audiobooks")
            recommendedAudiobooks.forEach { audiobook ->
                val entity = ItemToEntityConverter.convertAudiobookToEntity(audiobook)
                audiobookClusterBuilder.addEntity(entity)
            }
            requestBuilder.addRecommendationCluster(audiobookClusterBuilder.build())
        }

        return requestBuilder.build()
    }
}
