package com.musically.studio.domain.models

data class Album(
    val id: String,
    val title: String,
    val artist: Artist,
    val coverUrl: String,
    val year: Int
)
