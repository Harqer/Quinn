package com.musically.studio.di

import com.musically.studio.ui.jetcaster.core.player.EpisodePlayer
import com.musically.studio.ui.jetcaster.core.player.RealEpisodePlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {
    @Provides
    @Singleton
    fun provideEpisodePlayer(realEpisodePlayer: RealEpisodePlayer): EpisodePlayer {
        return realEpisodePlayer
    }
}
