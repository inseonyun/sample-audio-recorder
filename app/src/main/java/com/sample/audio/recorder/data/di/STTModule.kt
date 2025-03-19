package com.sample.audio.recorder.data.di

import com.sample.audio.recorder.model.STTState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.Channel
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object STTModule {

    @Provides
    @Singleton
    fun provideSTTChannel(): Channel<STTState> = Channel(1)
}
