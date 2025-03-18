package com.sample.audio.recorder.data.di

import com.sample.audio.recorder.BuildConfig.BASE_URL_GROQ
import com.sample.audio.recorder.data.api.STTApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideSTTApi(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory,
    ): STTApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_GROQ)
            .addConverterFactory(converterFactory)
            .client(okHttpClient)
            .build()
            .create(STTApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDefaultOkhttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)
            .build()

    @Provides
    @Singleton
    fun provideConverterFactory(): Converter.Factory {
        return Json.asConverterFactory("application/json".toMediaType())
    }
}
