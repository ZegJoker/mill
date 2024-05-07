package coder.stanley.mill.core

import coder.stanley.mill.core.reducer.combineReducers
import coder.stanley.mill.core.reducer.createReducer
import coder.stanley.mill.core.reducer.createScopedReducer

/**
 * A [Feature] defines a set of functionalities in a specific area.
 */
abstract class Feature<Action, State, Event> : Reducer<Action, State, Event> {
    private val reducers: Reducer<Action, State, Event> by lazy {
        val builder = FeatureBuilder<Action, State, Event>()
        builder.buildFeature()
        val reducerList = builder.build()
        combineReducers(reducerList)
    }

    /**
     * Define the structure of this feature.
     */
    abstract fun FeatureBuilder<Action, State, Event>.buildFeature()

    final override fun reduce(
        action: Action,
        set: ((State) -> State) -> Unit,
        get: () -> State,
    ): Effect<Action, Event> {
        return reducers.reduce(action, set, get)
    }

    class FeatureBuilder<Action, State, Event> {
        private val reducers = mutableListOf<Reducer<Action, State, Event>>()

        /**
         * Append a existing [Reducer] into the Feature.
         *
         * @param reducer the pre-defined reducer
         */
        @Suppress("FunctionNaming")
        fun Append(reducer: Reducer<Action, State, Event>) {
            reducers.add(reducer)
        }

        /**
         * Construct a [Reducer] and then append it to the feature.
         *
         * @param block the reduce function
         */
        @Suppress("FunctionNaming")
        fun Reducer(
            block: (
                action: Action,
                set: ((State) -> State) -> Unit,
                get: () -> State,
            ) -> Effect<Action, Event>
        ) {
            reducers.add(createReducer(block))
        }

        /**
         * Convert the [Action] and [State] into the [InnerAction] and [InnerState].
         */
        @Suppress("FunctionNaming")
        fun <InnerAction, InnerEvent, InnerState> Scope(
            toInnerAction: (Action) -> InnerAction?,
            fromInnerAction: (InnerAction) -> Action,
            toInnerState: (State) -> InnerState,
            fromInnerState: (State, InnerState) -> State,
            fromInnerEvent: (InnerEvent) -> Event,
            reducerBuilder: () -> Reducer<InnerAction, InnerState, InnerEvent>
        ) {
            val reducer = createScopedReducer(
                toInnerAction,
                fromInnerAction,
                toInnerState,
                fromInnerState,
                fromInnerEvent,
                reducerBuilder
            )
            reducers.add(reducer)
        }

        internal fun build() = reducers
    }
}
