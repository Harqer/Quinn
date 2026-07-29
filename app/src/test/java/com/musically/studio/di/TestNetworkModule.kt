package com.musically.studio.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.musically.studio.network.ApiClient
import com.musically.studio.network.FakeApiClient
import com.musically.studio.fakes.MaveSessionManagerFake
import com.musically.studio.network.MaveSessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.OkHttpClient
import org.mockito.Mockito
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class]
)
object TestNetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    fun provideApiClient(): ApiClient {
        return FakeApiClient()
    }

    @Provides
    @Singleton
    fun provideMaveSessionManager(okHttpClient: OkHttpClient): MaveSessionManager {
        return MaveSessionManagerFake(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Mockito.mock(FirebaseAuth::class.java)
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return Mockito.mock(FirebaseDatabase::class.java)
    }

    @Provides
    @Singleton
    fun provideGeminiLiveManager(): com.musically.studio.network.GeminiLiveManager {
        val mockGeminiLive = Mockito.mock(com.musically.studio.network.GeminiLiveManager::class.java)
        Mockito.doReturn(kotlinx.coroutines.flow.MutableSharedFlow<org.json.JSONObject>()).`when`(mockGeminiLive).functionCalls
        Mockito.doReturn(kotlinx.coroutines.flow.MutableSharedFlow<String>()).`when`(mockGeminiLive).transcripts
        Mockito.doReturn(kotlinx.coroutines.flow.MutableSharedFlow<String>()).`when`(mockGeminiLive).thoughts
        Mockito.doReturn(kotlinx.coroutines.flow.MutableStateFlow(false)).`when`(mockGeminiLive).connectionState
        return mockGeminiLive
    }

    @Provides
    @Singleton
    fun provideStreamingApiClient(): com.musically.studio.network.StreamingApiClient {
        return Mockito.mock(com.musically.studio.network.StreamingApiClient::class.java)
    }
}
