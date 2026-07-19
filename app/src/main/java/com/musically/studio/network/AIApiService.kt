package com.musically.studio.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Header

interface AIApiService {
    @POST("api/generate")
    suspend fun generateMusicPrompts(
        @Body request: GenerateRequest
    ): Response<GenerateResponse>

    @POST("api/logs/gesture")
    suspend fun logGesture(
        @Body request: LogGestureRequest
    ): Response<Unit>

    @POST("api/logs/battery")
    suspend fun logBattery(
        @Body request: LogBatteryRequest
    ): Response<Unit>
}

data class GenerateRequest(val image: String)
data class GenerateResponse(val prompts: List<String>)
data class LogGestureRequest(val gesture: String)
data class LogBatteryRequest(val batteryLevel: Int, val isWearDetected: Boolean)
