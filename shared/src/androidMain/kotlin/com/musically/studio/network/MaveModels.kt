package com.musically.studio.network

import com.google.gson.annotations.SerializedName

data class MaveArtist(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

data class MaveImage(
    @SerializedName("url") val url: String,
    @SerializedName("height") val height: Int? = null,
    @SerializedName("width") val width: Int? = null
)

data class MaveAlbum(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("artists") val artists: List<MaveArtist>? = null,
    @SerializedName("images") val images: List<MaveImage>,
    @SerializedName("description") val description: String? = null,
    @SerializedName("likes") val likes: Int = 0,
    @SerializedName("durationMs") val durationMs: Long = 0L
)

data class MaveTrack(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("artists") val artists: List<MaveArtist>,
    @SerializedName("album") val album: MaveAlbum,
    @SerializedName("userId") val userId: String? = null, // Mave Creator UID if community track
    @SerializedName("durationMs") val durationMs: Long = 0L
)

data class MaveTrackItem(
    @SerializedName("track") val track: MaveTrack
)

data class MaveTracksResponse(
    @SerializedName("items") val items: List<MaveTrackItem>
)

data class MavePlaylist(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("coverUrl") val coverUrl: String?,
    @SerializedName("creator") val creator: String? = null,
    @SerializedName("tracks") val tracks: List<MaveTrack> = emptyList(),
    @SerializedName("likes") val likes: Int = 0,
    @SerializedName("durationMs") val durationMs: Long = 0L,
    @SerializedName("description") val description: String? = null
)

data class MaveCategory(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("colorHex") val colorHex: String?,
    @SerializedName("imageUrl") val imageUrl: String?
)

data class MavePodcast(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("publisher") val publisher: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("description") val description: String?
)

data class MaveAudiobook(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("narrator") val narrator: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("duration") val duration: Int?,
    @SerializedName("audioUrl") val audioUrl: String?
)
