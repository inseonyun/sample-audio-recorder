package com.sample.audio.recorder.data.di

import android.content.Context
import com.sample.audio.recorder.data.api.STTApi
import com.sample.audio.recorder.data.repository.GroqSTT
import com.sample.audio.recorder.data.repository.VoiceRecorder
import com.sample.audio.recorder.model.STTState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.Channel
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object STTModule {

    @Provides
    @Singleton
    fun provideGroqSTT(
        @ApplicationContext context: Context,
        state: Channel<STTState>,
        sttApi: STTApi,
    ): VoiceRecorder = GroqSTT(context, state, sttApi)

    @Provides
    @Singleton
    fun provideSTTChannel(): Channel<STTState> = Channel(1)
}
