package com.sample.audio.recorder.data.api

import com.sample.audio.recorder.BuildConfig.GROQ_KEY
import com.sample.audio.recorder.data.api.model.GroqSTTResponse
import okhttp3.MultipartBody
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface STTApi {
    @Multipart
    @Headers("Authorization: Bearer $GROQ_KEY")
    @POST("v1/audio/transcriptions")
    suspend fun getGroqSTT(
        @Part file: MultipartBody.Part,
        @Part model: MultipartBody.Part,
    ): GroqSTTResponse
}
