package com.musically.studio.engage

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.engage.service.AppEngagePublishClient
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
            
            // Fetch real data from API
            val apiClient = com.musically.studio.network.RealApiClient(okhttp3.OkHttpClient())
            
            // Get user's own tracks for Continuation (recently played/generated)
            val recentTracks = apiClient.getUserTracks() ?: emptyList()
            
            val playlists = apiClient.getPlaylists() ?: emptyList()
            val recommendedPlaylists = playlists.take(1)
            val recommendedPodcasts = apiClient.getPodcasts() ?: emptyList()
            val recommendedAudiobooks = apiClient.getAudiobooks() ?: emptyList()

            if (publishClient.isServiceAvailable().await()) {
                // Publish Continuation Cluster
                val continuationRequest = ClusterRequestFactory.createContinuationClusterRequest(recentTracks)
                publishClient.publishContinuationCluster(continuationRequest)

                // Publish Recommendation Cluster
                val recommendationRequest = ClusterRequestFactory.createRecommendationClusterRequest(
                    recommendedPlaylists,
                    recommendedPodcasts,
                    recommendedAudiobooks
                )
                publishClient.publishRecommendationClusters(recommendationRequest)
                
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            if (runAttemptCount < Constants.MAX_ATTEMPT_COUNT) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
