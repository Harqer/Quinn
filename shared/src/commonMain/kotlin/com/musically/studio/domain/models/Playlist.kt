package com.musically.studio.domain.models

data class Playlist(
    val id: String,
    val title: String,
    val creator: String,
    val coverUrl: String,
    val tracks: List<Track>
)
