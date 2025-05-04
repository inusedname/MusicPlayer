package dev.keego.musicplayer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapNotNull

abstract class BaseViewModel<S: Any?, E: Any>: ViewModel() {
    private val _state = MutableStateFlow(initialState())
    val state = _state.asStateFlow()
    private val _event = MutableSharedFlow<E>()
    val event = _event.asSharedFlow()

    abstract fun initialState(): S

    fun <T: Any?> collectStateByChild(selector: (S) -> T): Flow<T> {
        return state.mapNotNull(selector)
    }

    protected fun setState(updater: (S) -> S) {
        _state.value = updater(state.value)
    }

    protected fun setState(newState: S) {
        _state.value = newState
    }

    protected suspend fun publishEvent(e: E) {
        _event.emit(e)
    }
}