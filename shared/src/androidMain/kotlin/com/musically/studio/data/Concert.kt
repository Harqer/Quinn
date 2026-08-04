package com.musically.studio.data

data class Concert(
    val id: String,
    val title: String,
    val url: String,
    val datetimeLocal: String,
    val venueName: String,
    val location: String,
    val imageUrl: String? = null
)
