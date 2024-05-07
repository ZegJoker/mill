package coder.stanley.mill.core

interface Reducer<Action, State, Event> {

    fun reduce(
        action: Action,
        set: ((current: State) -> State) -> Unit,
        get: () -> State
    ): Effect<Action, Event>
}
