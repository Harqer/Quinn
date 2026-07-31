package com.musically.studio.domain.models

data class Artist(
    val id: String,
    val name: String,
    val profileImageUrl: String? = null
)
