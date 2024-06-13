package coder.stanley.mill.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * State store is the place where stores the current state.
 */
expect open class StateStore<Action, State, Event> {

    /**
     * The current state.
     */
    val state: StateFlow<State>

    /**
     * The events throws by the [Reducer].
     */
    val event: SharedFlow<Event>

    /**
     * Dispatch the action to [Reducer] for processing.
     */
    fun dispatch(action: Action)

    /**
     * Callback method which will be called when store stopped.
     */
    open fun onClear()
}

/**
 * Create or get a store by the given name.
 *
 * @param name the name of the store, this name should be unique.
 * @param feature the feature which handles the actions.
 * @param initialState the function to generate the initial state
 */
@Composable
expect fun <Action, State, Event> rememberStore(
    name: String,
    feature: Feature<Action, State, Event>,
    initialState: () -> State,
): StateStore<Action, State, Event>

/**
 * A compose function which will invoke [block] every time a new [Event] comes.
 */
@Composable
fun <Action, State, Event> StateStore<Action, State, Event>.onEvent(block: (Event) -> Unit) {
    val callback by rememberUpdatedState(block)

    LaunchedEffect(Unit) {
        event.collect {
            callback(it)
        }
    }
}
