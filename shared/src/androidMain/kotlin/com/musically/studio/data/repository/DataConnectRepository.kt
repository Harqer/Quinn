package com.musically.studio.data.repository

import com.musically.studio.dataconnect.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

@Singleton
class DataConnectRepository @Inject constructor(
    private val connector: DefaultConnector
) {
    fun getPlaylists(): Flow<List<GetPlaylistsQuery.Data.PlaylistsItem>> = flow {
        try {
            val response = connector.getPlaylists.execute()
            emit(response.data.playlists)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch playlists from Data Connect")
            emit(emptyList())
        }
    }

    fun getAlbums(): Flow<List<GetAlbumsQuery.Data.AlbumsItem>> = flow {
        try {
            val response = connector.getAlbums.execute()
            emit(response.data.albums)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch albums")
            emit(emptyList())
        }
    }

    fun getCategories(): Flow<List<GetCategoriesQuery.Data.CategoriesItem>> = flow {
        try {
            val response = connector.getCategories.execute()
            emit(response.data.categories)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch categories")
            emit(emptyList())
        }
    }

    fun getPodcasts(): Flow<List<GetPodcastsQuery.Data.ShowsItem>> = flow {
        try {
            val response = connector.getPodcasts.execute()
            emit(response.data.shows)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch podcasts")
            emit(emptyList())
        }
    }

    fun getAudiobooks(): Flow<List<GetAudiobooksQuery.Data.AudiobooksItem>> = flow {
        try {
            val response = connector.getAudiobooks.execute()
            emit(response.data.audiobooks)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch audiobooks")
            emit(emptyList())
        }
    }

    fun getCommunityTracks(): Flow<List<GetCommunityTracksQuery.Data.TracksItem>> = flow {
        try {
            val response = connector.getCommunityTracks.execute()
            emit(response.data.tracks)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch community tracks")
            emit(emptyList())
        }
    }

    fun getUserTracks(): Flow<List<GetUserTracksQuery.Data.TracksItem>> = flow {
        try {
            val response = connector.getUserTracks.execute()
            emit(response.data.tracks)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch user tracks from DC")
            emit(emptyList())
        }
    }

    fun getLikedTracks(): Flow<List<GetLikedTracksQuery.Data.LikedTracksItem>> = flow {
        try {
            val response = connector.getLikedTracks.execute()
            emit(response.data.likedTracks)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch liked tracks from DC")
            emit(emptyList())
        }
    }

    fun getBookmarkedTracks(): Flow<List<GetBookmarkedTracksQuery.Data.BookmarkedTracksItem>> = flow {
        try {
            val response = connector.getBookmarkedTracks.execute()
            emit(response.data.bookmarkedTracks)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch bookmarked tracks from DC")
            emit(emptyList())
        }
    }

    suspend fun createTrack(title: String, albumId: String, audioUrl: String, isCommunity: Boolean) {
        try {
            connector.createTrack.execute(
                title = title,
                audioUrl = audioUrl,
                isCommunity = isCommunity
            ) {
                this.albumId = albumId
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to create track")
        }
    }

    suspend fun bookmarkTrack(trackId: String): Boolean {
        return try {
            connector.bookmarkTrack.execute(trackId = trackId)
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to bookmark track")
            false
        }
    }
    
    suspend fun likeTrack(trackId: String): Boolean {
        return try {
            connector.likeTrack.execute(trackId = trackId)
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to like track")
            false
        }
    }

    suspend fun unlikeTrack(trackId: String): Boolean {
        return try {
            connector.removeLikedTrack.execute(trackId = trackId)
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to unlike track")
            false
        }
    }

    suspend fun addTrackToPlaylist(trackId: String, playlistId: String): Boolean {
        return try {
            connector.addTrackToPlaylist.execute(trackId = trackId, playlistId = playlistId)
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to add track to playlist")
            false
        }
    }

    fun searchTracks(query: String): Flow<List<SearchTracksQuery.Data.TracksItem>> = flow {
        try {
            val response = connector.searchTracks.execute(query = query)
            emit(response.data.tracks)
        } catch (e: Exception) {
            Timber.e(e, "Failed to search tracks")
            emit(emptyList())
        }
    }

    suspend fun getTrack(trackId: String): GetTrackQuery.Data.Track? {
        return try {
            val response = connector.getTrack.execute(id = trackId)
            response.data.track
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch track")
            null
        }
    }
}
