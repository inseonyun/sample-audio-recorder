package com.sample.audio.recorder.data.di

import com.sample.audio.recorder.data.repository.GroqSTT
import com.sample.audio.recorder.data.repository.GroqSTTRepository
import com.sample.audio.recorder.data.repository.STTRepository
import com.sample.audio.recorder.data.repository.VoiceRecorder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindSTTRepository(
        groqSTTRepository: GroqSTTRepository
    ): STTRepository

    @Binds
    @Singleton
    fun provideGroqSTT(
        groqSTT: GroqSTT,
    ): VoiceRecorder
}
