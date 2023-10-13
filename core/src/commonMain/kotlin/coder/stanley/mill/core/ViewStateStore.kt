package coder.stanley.mill.core

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

expect open class ViewStateStore<Action, State, Effect> {

    val state: StateFlow<State>

    val effect: SharedFlow<Effect>

    fun dispatch(action: Action)

    open fun onClear()
}

@Composable
expect fun <Action, State, Effect> rememberStore(
    reducer: Reducer<Action, State, Effect>,
    name: String = reducer::class.qualifiedName ?: "",
    initialState: () -> State,
): ViewStateStore<Action, State, Effect>

@Composable
fun <Action, State, Effect> rememberStore(
    reducer: NamedReducer<Action, State, Effect>,
    initialState: () -> State,
): ViewStateStore<Action, State, Effect> =
    rememberStore(reducer = reducer, name = reducer.name, initialState = initialState)
