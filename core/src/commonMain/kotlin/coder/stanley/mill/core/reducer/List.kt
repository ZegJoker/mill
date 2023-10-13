package coder.stanley.mill.core.reducer

import coder.stanley.mill.core.NamedReducer
import coder.stanley.mill.core.Reducer

private class ListReducer<Action, State, Effect>(
    override val name: String,
    private val reducers: List<Reducer<Action, State, Effect>>,
) : NamedReducer<Action, State, Effect> {
    override suspend fun reduce(action: Action, currentState: State, onEffect: (Effect) -> Unit): State {
        var state = currentState
        reducers.forEach {
            state = it.reduce(action, state, onEffect)
        }
        return state
    }
}

fun <Action, State, Effect> combineReducers(
    name: String,
    vararg reducers: Reducer<Action, State, Effect>
): NamedReducer<Action, State, Effect> {
    return ListReducer(name = name, reducers = reducers.toList())
}
