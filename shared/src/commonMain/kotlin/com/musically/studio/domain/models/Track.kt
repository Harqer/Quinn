package com.musically.studio.domain.models

data class Track(
    val id: String,
    val title: String,
    val artist: Artist,
    val album: Album? = null,
    val durationMs: Long,
    val coverUrl: String,
    val videoUrl: String? = null,
    val streamUrl: String,
    val isLiked: Boolean = false
)
