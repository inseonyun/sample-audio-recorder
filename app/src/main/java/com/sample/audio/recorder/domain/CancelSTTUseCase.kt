package com.sample.audio.recorder.domain

import com.sample.audio.recorder.data.repository.STTRepository
import javax.inject.Inject

class CancelSTTUseCase @Inject constructor(
    private val sttRepository: STTRepository,
) {
    operator fun invoke(): Result<Unit> = sttRepository.cancel()
}
