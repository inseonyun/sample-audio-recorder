package com.sample.audio.recorder.domain

import com.sample.audio.recorder.data.repository.STTRepository
import javax.inject.Inject

class EndSTTUseCase @Inject constructor(
    private val sttRepository: STTRepository,
) {
    suspend operator fun invoke(): Result<Unit> = sttRepository.end()
}
