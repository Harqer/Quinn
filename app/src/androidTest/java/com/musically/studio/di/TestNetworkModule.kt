package com.musically.studio.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.musically.studio.network.ApiClient
import com.musically.studio.network.FakeApiClient
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
        return FakeMaveSessionManager(okHttpClient)
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
}
