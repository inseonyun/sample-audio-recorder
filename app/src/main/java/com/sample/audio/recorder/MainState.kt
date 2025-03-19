package com.sample.audio.recorder

sealed interface MainState {
    data object Idle : MainState
    data class Success(val text: String) : MainState
    data object TimeOut : MainState
    data class Error(val message: String) : MainState
}
