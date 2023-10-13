package coder.stanley.mill.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

actual open class ViewStateStore<Action, State, Effect>(
    private val reducer: Reducer<Action, State, Effect>,
    initialState: () -> State
): ViewModel() {

    private val _state by lazy { MutableStateFlow(initialState()) }
    actual val state: StateFlow<State> by lazy { _state }

    private val _effect = MutableSharedFlow<Effect>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    actual val effect: SharedFlow<Effect> = _effect

    actual fun dispatch(action: Action) {
        viewModelScope.launch {
            val currentState = state.value
            _state.value = reducer.reduce(action, currentState) {
                _effect.tryEmit(it)
            }
        }
    }

    actual open fun onClear() {

    }
}

@Composable
actual fun <Action, State, Effect> rememberStore(
    reducer: Reducer<Action, State, Effect>,
    name: String,
    initialState: () -> State
): ViewStateStore<Action, State, Effect> {
    val storeSaver = LocalViewStateStoreSaver.current
    val store = storeSaver.getStore<Action, State, Effect>(name)
    return if (store == null) {
        remember { ViewStateStore(reducer, initialState).also { storeSaver.putStore(name, it) } }
    } else {
        remember { store }
    }
}
