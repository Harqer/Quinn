package com.musically.studio.di

import com.musically.studio.network.AIApiService
import com.musically.studio.network.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL = "https://musically-studio.run.app/" // Production URL

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val token = runBlocking { TokenManager.getValidToken() }
        
        val request = if (token != null) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }
        
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val aiApiService: AIApiService = retrofit.create(AIApiService::class.java)
}
