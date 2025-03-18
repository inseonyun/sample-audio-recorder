package com.sample.audio.recorder.model

sealed interface STTState {
    data class Success(val text: String) : STTState
    data object TimeOut : STTState
    data class Error(val message: String) : STTState
}
