package coder.stanley.mill.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

expect open class ViewStateStore<Action, State, Event> {

    val state: StateFlow<State>

    val event: SharedFlow<Event>

    fun dispatch(action: Action)

    open fun onClear()
}

@Composable
expect fun <Action, State, Event> rememberStore(
    name: String,
    feature: Feature<Action, State, Event>,
    initialState: () -> State,
): ViewStateStore<Action, State, Event>

@Composable
fun <Action, State, Event> ViewStateStore<Action, State, Event>.onEvent(block: (Event) -> Unit) {
    val callback by rememberUpdatedState(block)

    LaunchedEffect(Unit) {
        event.collect {
            callback(it)
        }
    }
}
