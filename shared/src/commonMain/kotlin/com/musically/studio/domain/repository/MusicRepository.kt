package com.musically.studio.domain.repository

import com.musically.studio.domain.models.Album
import com.musically.studio.domain.models.Playlist
import com.musically.studio.domain.models.Track

interface MusicRepository {
    suspend fun getDiscoverFeed(): List<Any> // Mixed Tracks, Albums, Playlists
    suspend fun getRecentTracks(): List<Track>
    suspend fun getLibraryPlaylists(): List<Playlist>
    suspend fun search(query: String): List<Track>
}
