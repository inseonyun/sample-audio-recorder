package com.sample.audio.recorder.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class XGroqResponse(
    @SerialName("id") val id: String,
)
