package com.example.jecpackcomposeno1.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class MviViewModel<STATE, INTENT, EFFECT>(
    initialState: STATE,
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<STATE> = _uiState.asStateFlow()

    private val _effect = Channel<EFFECT>(Channel.BUFFERED)
    val effect: Flow<EFFECT> = _effect.receiveAsFlow()

    /** Cửa duy nhất từ UI: gửi Intent, không gọi hàm lung tung */
    fun onIntent(intent: INTENT) {
        handleIntent(intent)
    }

    protected abstract fun handleIntent(intent: INTENT)

    protected fun setState(reducer: STATE.() -> STATE) {
        _uiState.update { it.reducer() }
    }

    protected fun sendEffect(effect: EFFECT) {
        viewModelScope.launch { _effect.send(effect) }
    }

    protected fun launchTask(
        onLoading: () -> Unit = {},
        onError: (Throwable) -> Unit = {},
        block: suspend () -> Unit,
    ) {
        viewModelScope.launch {
            onLoading()
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
