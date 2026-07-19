package com.musically.studio.network

import com.google.gson.annotations.SerializedName

data class SpotifyArtist(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

data class SpotifyImage(
    @SerializedName("url") val url: String,
    @SerializedName("height") val height: Int? = null,
    @SerializedName("width") val width: Int? = null
)

data class SpotifyAlbum(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("images") val images: List<SpotifyImage>
)

data class SpotifyTrack(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("artists") val artists: List<SpotifyArtist>,
    @SerializedName("album") val album: SpotifyAlbum
)

data class SpotifyTrackItem(
    @SerializedName("track") val track: SpotifyTrack
)

data class SpotifyTracksResponse(
    @SerializedName("items") val items: List<SpotifyTrackItem>
)
