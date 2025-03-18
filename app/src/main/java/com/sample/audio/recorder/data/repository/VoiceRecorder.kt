package com.sample.audio.recorder.data.repository

import com.sample.audio.recorder.model.STTState
import kotlinx.coroutines.channels.Channel

abstract class VoiceRecorder(
    val state: Channel<STTState>,
) {
    abstract suspend fun create()
    abstract suspend fun start()
    abstract fun cancel()
    open suspend fun end() = Unit
}
