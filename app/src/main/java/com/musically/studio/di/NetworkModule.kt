package com.musically.studio.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.musically.studio.network.ApiClient
import com.musically.studio.network.RealApiClient
import com.musically.studio.network.MaveSessionManager
import com.musically.studio.network.GeminiLiveManager
import com.musically.studio.network.StreamingApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    fun provideApiClient(okHttpClient: OkHttpClient): ApiClient {
        return RealApiClient(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideStreamingApiClient(okHttpClient: OkHttpClient): StreamingApiClient {
        return StreamingApiClient(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideMaveSessionManager(okHttpClient: OkHttpClient): MaveSessionManager {
        return MaveSessionManager(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideGeminiLiveManager(okHttpClient: OkHttpClient): GeminiLiveManager {
        return GeminiLiveManager(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }
}
