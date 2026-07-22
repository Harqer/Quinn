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
    @SerializedName("images") val images: List<MaveImage>
)

data class MaveTrack(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("artists") val artists: List<MaveArtist>,
    @SerializedName("album") val album: MaveAlbum,
    @SerializedName("userId") val userId: String? = null // Mave Creator UID if community track
)

data class MaveTrackItem(
    @SerializedName("track") val track: MaveTrack
)

data class MaveTracksResponse(
    @SerializedName("items") val items: List<MaveTrackItem>
)
