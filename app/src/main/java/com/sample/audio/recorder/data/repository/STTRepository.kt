package com.sample.audio.recorder.data.repository

import com.sample.audio.recorder.model.STTState
import kotlinx.coroutines.flow.Flow

interface STTRepository {
    suspend fun create(): Result<Unit>
    suspend fun start(): Result<Unit>
    suspend fun end(): Result<Unit>
    fun cancel(): Result<Unit>
    fun getSTTState(): Flow<STTState>
}
