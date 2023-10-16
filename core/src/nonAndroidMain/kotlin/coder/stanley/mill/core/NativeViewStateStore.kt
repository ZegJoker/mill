package coder.stanley.mill.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

actual open class ViewStateStore<Action, State, Effect>(
    private val reducer: Reducer<Action, State, Effect>,
    initialState: () -> State,
) : CoroutineScope {
    private val _state by lazy { MutableStateFlow(initialState()) }
    actual val state: StateFlow<State> by lazy { _state }

    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()

    private val _effect = MutableSharedFlow<Effect>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    actual val effect: SharedFlow<Effect> = _effect

    actual fun dispatch(action: Action) {
        launch {
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
    initialState: () -> State,
): ViewStateStore<Action, State, Effect> {
    val storeSaver = LocalViewStateStoreSaver.current
    return remember {
        storeSaver.getStore(name) ?: ViewStateStore(
            reducer,
            initialState
        ).also { storeSaver.putStore(name, it) }
    }
}

