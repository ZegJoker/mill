package coder.stanley.mill.core.reducer

import coder.stanley.mill.core.Effect
import coder.stanley.mill.core.Reducer

fun <Action, State, Event> createReducer(
    block: (
        action: Action,
        set: ((current: State) -> State) -> Unit,
        get: () -> State
    ) -> Effect<Action, Event>,
): Reducer<Action, State, Event> {
    return object : Reducer<Action, State, Event> {
        override fun reduce(
            action: Action,
            set: ((current: State) -> State) -> Unit,
            get: () -> State
        ): Effect<Action, Event> {
            return block(action, set, get)
        }
    }
}

fun <Action, InnerAction, Event, InnerEvent, State, InnerState> createScopedReducer(
    toInnerAction: (Action) -> InnerAction?,
    fromInnerAction: (InnerAction) -> Action,
    toInnerState: (State) -> InnerState,
    fromInnerState: (State, InnerState) -> State,
    fromInnerEvent: (InnerEvent) -> Event,
    reducerBuilder: () -> Reducer<InnerAction, InnerState, InnerEvent>
): Reducer<Action, State, Event> {
    return object: Reducer<Action, State, Event> {
        val innerReducer by lazy { reducerBuilder() }
        override fun reduce(
            action: Action,
            set: ((current: State) -> State) -> Unit,
            get: () -> State
        ): Effect<Action, Event> {
            val innerAction = toInnerAction(action) ?: return Effect.none()
            val innerEffect = innerReducer.reduce(
                innerAction,
                set = { newInnerState ->
                    set { state ->
                        fromInnerState(
                            state,
                            newInnerState(toInnerState(state))
                        )
                    }
                },
                get = { toInnerState(get()) }
            )
            return mapEffect(innerEffect)
        }

        private fun mapEffect(innerEffect: Effect<InnerAction, InnerEvent>): Effect<Action, Event> {
            return when (innerEffect) {
                is Effect.None -> Effect.none()
                is Effect.EventEmitter -> Effect.event(fromInnerEvent(innerEffect.event))
                is Effect.Task -> Effect.task(innerEffect.id) { send ->
                    innerEffect.execute { send(fromInnerAction(it)) }
                }

                is Effect.CancelTask -> Effect.cancel(innerEffect.id)
                is Effect.ListEffect -> Effect.ListEffect(innerEffect.effects.map(::mapEffect))
            }
        }
    }
}
