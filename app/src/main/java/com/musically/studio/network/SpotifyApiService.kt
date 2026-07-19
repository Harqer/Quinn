package com.musically.studio.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface SpotifyApiService {
    @GET("v1/me/tracks")
    fun getSavedTracks(@Header("Authorization") authorization: String): Call<SpotifyTracksResponse>
}
