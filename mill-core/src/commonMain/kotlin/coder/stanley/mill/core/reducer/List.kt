package coder.stanley.mill.core.reducer

import coder.stanley.mill.core.Effect
import coder.stanley.mill.core.Reducer

internal class ListReducer<Action, State, Event>(
    private val reducers: List<Reducer<Action, State, Event>>,
) : Reducer<Action, State, Event> {
    override fun reduce(
        action: Action,
        set: ((current: State) -> State) -> Unit,
        get: () -> State
    ): Effect<Action, Event> {
        val effects = reducers.map {
            it.reduce(action, set, get)
        }.filter { it !is Effect.None }
        return Effect.ListEffect(effects)
    }
}

fun <Action, State, Event> combineReducers(
    vararg reducers: Reducer<Action, State, Event>
): Reducer<Action, State, Event> {
    return combineReducers(reducers.toList())
}

fun <Action, State, Event> combineReducers(
    reducers: List<Reducer<Action, State, Event>>
): Reducer<Action, State, Event> {
    return ListReducer(reducers = reducers)
}
