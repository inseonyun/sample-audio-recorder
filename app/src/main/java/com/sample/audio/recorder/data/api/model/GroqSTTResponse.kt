package com.sample.audio.recorder.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroqSTTResponse(
    @SerialName("text") val text: String,
    @SerialName("x_groq") val xGroq: XGroqResponse,
)
