package coder.stanley.mill.core.reducer

import coder.stanley.mill.core.NamedReducer

fun <Action, State, Effect> createReducer(
    name: String,
    block: suspend (action: Action, currentState: State, onEffect: (Effect) -> Unit) -> State,
): NamedReducer<Action, State, Effect> {
    return object : NamedReducer<Action, State, Effect> {
        override val name: String = name

        override suspend fun reduce(action: Action, currentState: State, onEffect: (Effect) -> Unit): State {
            return block(action, currentState, onEffect)
        }
    }
}
