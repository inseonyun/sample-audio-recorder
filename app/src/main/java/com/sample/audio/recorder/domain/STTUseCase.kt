package com.sample.audio.recorder.domain

import com.sample.audio.recorder.model.STTState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class STTUseCase @Inject constructor(
    private val createSTTUseCase: CreateSTTUseCase,
    private val startSTTUseCase: StartSTTUseCase,
    private val endSTTUseCase: EndSTTUseCase,
    private val cancelSTTUseCase: CancelSTTUseCase,
    private val getSTTUseCase: GetSTTUseCase,
) {
    suspend fun create(): Result<Unit> = createSTTUseCase()
    suspend fun start(): Result<Unit> = startSTTUseCase()
    suspend fun end(): Result<Unit> = endSTTUseCase()
    fun cancel(): Result<Unit> = cancelSTTUseCase()
    fun getSTTState(): Flow<STTState> = getSTTUseCase()
}
