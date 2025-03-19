package com.sample.audio.recorder.domain

import com.sample.audio.recorder.data.repository.STTRepository
import com.sample.audio.recorder.model.STTState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSTTUseCase @Inject constructor(
    private val sttRepository: STTRepository,
) {
    operator fun invoke(): Flow<STTState> = sttRepository.getSTTState()
}
