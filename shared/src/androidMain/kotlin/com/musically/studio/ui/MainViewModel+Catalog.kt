/**
 * @AtomicLevel: Template/Page
 * @SemanticPurpose: Android Component for MainViewModel+Catalog.kt
 */

package com.musically.studio.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.musically.studio.network.MaveTrack
import com.musically.studio.network.MaveAlbum
import com.musically.studio.network.MaveArtist
import com.musically.studio.network.MaveImage

import kotlinx.coroutines.flow.combine

fun MainViewModel.searchCatalog(query: String) {
    if (query.isBlank()) {
        _searchResults.value = emptyList()
        return
    }
    viewModelScope.launch {
        _isLoading.value = true
        combine(
            dataConnectRepository.searchTracks(query),
            dataConnectRepository.searchPodcasts(query),
            dataConnectRepository.searchAudiobooks(query)
        ) { tracks, podcasts, audiobooks ->
            val mappedTracks = tracks.map { item ->
                MaveTrack(
                    id = item.id,
                    name = item.title,
                    album = MaveAlbum(
                        id = item.album?.id ?: "",
                        name = item.album?.title ?: "",
                        artists = listOf(
                            MaveArtist(
                                id = item.album?.primaryArtist?.id ?: "",
                                name = item.album?.primaryArtist?.name ?: ""
                            )
                        ),
                        images = listOf(
                            MaveImage(url = item.coverUrl ?: "")
                        )
                    ),
                    artists = listOf(
                        MaveArtist(
                            id = item.album?.primaryArtist?.id ?: "",
                            name = item.album?.primaryArtist?.name ?: ""
                        )
                    ),
                    audioUrl = item.audioUrl ?: ""
                )
            }
            val mappedPodcasts = podcasts.map { item ->
                MaveTrack(
                    id = item.id,
                    name = item.title,
                    album = MaveAlbum(
                        id = item.id,
                        name = "Podcast",
                        artists = listOf(MaveArtist(id = "publisher", name = item.publisher ?: "Unknown")),
                        images = listOf(MaveImage(url = item.coverUrl ?: ""))
                    ),
                    artists = listOf(MaveArtist(id = "publisher", name = item.publisher ?: "Unknown")),
                    audioUrl = ""
                )
            }
            val mappedAudiobooks = audiobooks.map { item ->
                MaveTrack(
                    id = item.id,
                    name = item.title,
                    album = MaveAlbum(
                        id = item.id,
                        name = "Audiobook",
                        artists = listOf(MaveArtist(id = item.author?.id ?: "", name = item.author?.name ?: "Unknown")),
                        images = listOf(MaveImage(url = item.coverUrl ?: ""))
                    ),
                    artists = listOf(MaveArtist(id = item.author?.id ?: "", name = item.author?.name ?: "Unknown")),
                    audioUrl = ""
                )
            }
            mappedTracks + mappedPodcasts + mappedAudiobooks
        }.collect { combinedResults ->
            _searchResults.value = combinedResults
            _isLoading.value = false
        }
    }
}

fun MainViewModel.exploreCatalog() {
    fetchCatalog()
}
