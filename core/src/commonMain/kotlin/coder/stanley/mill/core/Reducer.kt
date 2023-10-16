package coder.stanley.mill.core

interface Reducer<Action, State, Effect> {

    suspend fun reduce(action: Action, currentState: State, onEffect: (Effect) -> Unit): State
}

interface NamedReducer<Action, State, Effect> : Reducer<Action, State, Effect> {
    val name: String
}
