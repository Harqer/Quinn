package com.musically.studio.di

import com.google.firebase.auth.FirebaseAuth
import com.musically.studio.dataconnect.DefaultConnector
import com.musically.studio.dataconnect.getInstance
import com.musically.studio.data.repository.DataConnectRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDefaultConnector(): DefaultConnector {
        return DefaultConnector.getInstance()
    }

    @Provides
    @Singleton
    fun provideDataConnectRepository(connector: DefaultConnector): DataConnectRepository {
        return DataConnectRepository(connector)
    }

}
