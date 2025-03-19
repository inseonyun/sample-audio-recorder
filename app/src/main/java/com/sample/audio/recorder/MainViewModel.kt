package com.sample.audio.recorder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.audio.recorder.domain.STTUseCase
import com.sample.audio.recorder.model.STTState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sttUseCase: STTUseCase,
) : ViewModel() {

    private val _mainState: MutableStateFlow<MainState> = MutableStateFlow(MainState.Idle)
    val mainState: StateFlow<MainState> = _mainState.asStateFlow()

    init {
        create()
        collectSTTState()
    }

    private fun collectSTTState() {
        viewModelScope.launch(Dispatchers.IO) {
            sttUseCase.getSTTState().collect { state ->
                when (state) {
                    is STTState.Success -> _mainState.value = MainState.Success(state.text)
                    is STTState.TimeOut -> _mainState.value = MainState.TimeOut
                    is STTState.Error -> _mainState.value = MainState.Error(state.message)
                }
            }
        }
    }

    private fun create() {
        viewModelScope.launch(Dispatchers.IO) {
            sttUseCase.create()
        }
    }

    fun start() {
        viewModelScope.launch(Dispatchers.IO) {
            sttUseCase.start()
        }
    }

    fun end() {
        viewModelScope.launch(Dispatchers.IO) {
            sttUseCase.end()
        }
    }

    fun cancel() {
        viewModelScope.launch(Dispatchers.IO) {
            sttUseCase.cancel()
        }
    }
}
