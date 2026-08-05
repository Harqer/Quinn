package com.musically.studio.network

import com.musically.studio.dataconnect.GetUserTracksQuery
import com.musically.studio.dataconnect.GetLikedTracksQuery
import com.musically.studio.dataconnect.GetCommunityTracksQuery
import com.musically.studio.dataconnect.GetPlaylistsQuery
import com.musically.studio.dataconnect.GetCategoriesQuery
import com.musically.studio.dataconnect.GetAudiobooksQuery
import com.musically.studio.dataconnect.GetPodcastsQuery

fun GetUserTracksQuery.Data.TracksItem.toMaveTrack(): MaveTrack {
    return MaveTrack(
        id = this.id.toString(),
        name = this.title,
        artists = listOf(MaveArtist(id = this.album?.primaryArtist?.id?.toString() ?: "", name = this.album?.primaryArtist?.name ?: "Unknown")),
        album = MaveAlbum(id = this.album?.id?.toString() ?: "", name = this.album?.title ?: "Unknown", images = listOf(MaveImage(url = this.coverUrl ?: ""))),
        audioUrl = this.audioUrl
    )
}

fun GetLikedTracksQuery.Data.LikedTracksItem.toMaveTrack(): MaveTrack {
    return MaveTrack(
        id = this.track.id.toString(),
        name = this.track.title,
        artists = listOf(MaveArtist(id = this.track.album?.primaryArtist?.id?.toString() ?: "", name = this.track.album?.primaryArtist?.name ?: "Unknown")),
        album = MaveAlbum(id = this.track.album?.id?.toString() ?: "", name = this.track.album?.title ?: "Unknown", images = listOf(MaveImage(url = this.track.coverUrl ?: ""))),
        audioUrl = this.track.audioUrl
    )
}

fun GetCommunityTracksQuery.Data.TracksItem.toMaveTrack(): MaveTrack {
    return MaveTrack(
        id = this.id.toString(),
        name = this.title,
        artists = listOf(MaveArtist(id = this.album?.primaryArtist?.id?.toString() ?: "", name = this.album?.primaryArtist?.name ?: "Unknown")),
        album = MaveAlbum(id = this.album?.id?.toString() ?: "", name = this.album?.title ?: "Unknown", images = listOf(MaveImage(url = this.coverUrl ?: ""))),
        audioUrl = null
    )
}

fun GetPlaylistsQuery.Data.PlaylistsItem.toMavePlaylist(): MavePlaylist {
    return MavePlaylist(
        id = this.id.toString(),
        name = this.name,
        coverUrl = this.coverUrl ?: ""
    )
}

fun GetCategoriesQuery.Data.CategoriesItem.toMaveCategory(): MaveCategory {
    return MaveCategory(
        id = this.id.toString(),
        name = this.name,
        colorHex = "#000000",
        imageUrl = null
    )
}

fun GetAudiobooksQuery.Data.AudiobooksItem.toMaveAudiobook(): MaveAudiobook {
    return MaveAudiobook(
        id = this.id.toString(),
        title = this.title,
        author = this.author.name,
        narrator = this.narrator,
        imageUrl = this.coverUrl ?: "",
        duration = this.totalDurationMs,
        audioUrl = null
    )
}

fun GetPodcastsQuery.Data.ShowsItem.toMavePodcast(): MavePodcast {
    return MavePodcast(
        id = this.id.toString(),
        name = this.title,
        publisher = this.publisher,
        imageUrl = this.coverUrl ?: "",
        description = this.description ?: ""
    )
}
