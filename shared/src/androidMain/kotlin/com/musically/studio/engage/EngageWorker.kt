package com.musically.studio.engage

import android.content.Context
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.engage.audio.datamodel.MusicTrackEntity
import com.google.android.engage.audio.datamodel.PlaylistEntity
import com.google.android.engage.audio.datamodel.PodcastSeriesEntity
import com.google.android.engage.books.datamodel.AudiobookEntity
import com.google.android.engage.common.datamodel.ClusterType
import com.google.android.engage.common.datamodel.ContinuationCluster
import com.google.android.engage.common.datamodel.Image
import com.google.android.engage.common.datamodel.RecommendationCluster
import com.google.android.engage.service.AppEngagePublishClient
import com.google.android.engage.service.PublishContinuationClusterRequest
import com.google.android.engage.service.PublishRecommendationClustersRequest
import com.google.android.engage.service.ServiceAvailabilityRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

class EngageWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val publishClient = AppEngagePublishClient(applicationContext)
            val apiClient = com.musically.studio.network.RealApiClient(okhttp3.OkHttpClient())
            
            val recentTracks = apiClient.getUserTracks() ?: emptyList()
            val recommendedPlaylists = (apiClient.getPlaylists() ?: emptyList()).take(1)
            val recommendedPodcasts = apiClient.getPodcasts() ?: emptyList()
            val recommendedAudiobooks = apiClient.getAudiobooks() ?: emptyList()

            val request = ServiceAvailabilityRequest.Builder()
                .addIntendedClusterType(ClusterType.TYPE_CONTINUATION)
                .addIntendedClusterType(ClusterType.TYPE_RECOMMENDATION)
                .build()

            val availabilityMap = publishClient.isServiceAvailable(request).await()
            val isContinuationAvailable = availabilityMap[ClusterType.TYPE_CONTINUATION] == true
            val isRecommendationAvailable = availabilityMap[ClusterType.TYPE_RECOMMENDATION] == true

            if (isContinuationAvailable || isRecommendationAvailable) {
                if (isContinuationAvailable) {
                // Continuation Cluster
                val continuationBuilder = ContinuationCluster.Builder()
                recentTracks.forEach { track ->
                    val entity = MusicTrackEntity.Builder()
                        .setEntityId(track.id)
                        .setName(track.name)
                        .setPlayBackUri("musically://track/${track.id}".toUri())
                        .setDurationMillis(track.durationMs)
                        .addArtists(track.artists.map { it.name })
                        .apply {
                            track.album.images.firstOrNull()?.url?.takeIf { it.isNotBlank() }?.let {
                                addPosterImage(Image.Builder().setImageUri(it.toUri()).build())
                            }
                        }.build()
                    continuationBuilder.addEntity(entity)
                }
                publishClient.publishContinuationCluster(
                    PublishContinuationClusterRequest.Builder()
                        .setContinuationCluster(continuationBuilder.build())
                        .build()
                )
                }

                if (isRecommendationAvailable) {
                // Recommendation Clusters
                val recommendationRequest = PublishRecommendationClustersRequest.Builder()
                
                val playlistClusterBuilder = RecommendationCluster.Builder().setTitle("Recommended Playlists")
                recommendedPlaylists.forEach { playlist ->
                    val entity = PlaylistEntity.Builder()
                        .setEntityId(playlist.id)
                        .setName(playlist.name)
                        .setPlayBackUri("musically://playlist/${playlist.id}".toUri())
                        .setSongsCount(playlist.tracks.size)
                        .setDurationMillis(playlist.durationMs)
                        .apply {
                            playlist.description?.let { setDescription(it) }
                            playlist.coverUrl?.takeIf { it.isNotBlank() }?.let {
                                addPosterImage(Image.Builder().setImageUri(it.toUri()).build())
                            }
                        }.build()
                    playlistClusterBuilder.addEntity(entity)
                }
                recommendationRequest.addRecommendationCluster(playlistClusterBuilder.build())

                if (recommendedPodcasts.isNotEmpty()) {
                    val podcastClusterBuilder = RecommendationCluster.Builder().setTitle("Recommended Podcasts")
                    recommendedPodcasts.forEach { podcast ->
                        val entity = PodcastSeriesEntity.Builder()
                            .setEntityId(podcast.id)
                            .setName(podcast.name)
                            .setPlayBackUri("musically://podcast/${podcast.id}".toUri())
                            .setInfoPageUri("musically://podcast/${podcast.id}/info".toUri())
                            .apply {
                                podcast.description?.let { setDescription(it) }
                                podcast.imageUrl?.takeIf { it.isNotBlank() }?.let {
                                    addPosterImage(Image.Builder().setImageUri(it.toUri()).build())
                                }
                            }.build()
                        podcastClusterBuilder.addEntity(entity)
                    }
                    recommendationRequest.addRecommendationCluster(podcastClusterBuilder.build())
                }

                if (recommendedAudiobooks.isNotEmpty()) {
                    val audiobookClusterBuilder = RecommendationCluster.Builder().setTitle("Recommended Audiobooks")
                    recommendedAudiobooks.forEach { audiobook ->
                        val entity = AudiobookEntity.Builder()
                            .setEntityId(audiobook.id)
                            .setName(audiobook.title)
                            .addAuthor(audiobook.author)
                            .setActionLinkUri("musically://audiobook/${audiobook.id}".toUri())
                            .apply {
                                audiobook.imageUrl?.takeIf { it.isNotBlank() }?.let {
                                    addPosterImage(Image.Builder().setImageUri(it.toUri()).build())
                                }
                            }.build()
                        audiobookClusterBuilder.addEntity(entity)
                    }
                    recommendationRequest.addRecommendationCluster(audiobookClusterBuilder.build())
                }

                publishClient.publishRecommendationClusters(recommendationRequest.build())
                }
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            if (runAttemptCount < Constants.MAX_ATTEMPT_COUNT) Result.retry() else Result.failure()
        }
    }
}

