package com.sample.audio.recorder.data.repository

import com.sample.audio.recorder.model.STTState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class GroqSTTRepository @Inject constructor(
    private val groqSTT: GroqSTT,
) : STTRepository {
    override suspend fun create(): Result<Unit> = runCatching {
        groqSTT.create()
    }

    override suspend fun start(): Result<Unit> = runCatching {
        groqSTT.start()
    }

    override suspend fun end(): Result<Unit> = runCatching {
        groqSTT.end()
    }

    override fun cancel(): Result<Unit> = runCatching {
        groqSTT.cancel()
    }

    override fun getSTTState(): Flow<STTState> = groqSTT.state.receiveAsFlow()
}
