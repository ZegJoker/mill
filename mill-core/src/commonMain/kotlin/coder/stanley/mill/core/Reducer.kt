package coder.stanley.mill.core

/**
 * A [Reducer] can be used to consume [Action] and update the state bases on it.
 */
interface Reducer<Action, State, Event> {

    /**
     * Process the given action.
     *
     * @param action the action that needs to be processed
     * @param set set the state to the given state
     * @param get get the current state
     *
     * @return An effect that can communicate with the outside world and feed actions back into
     *      the system.
     */
    fun reduce(
        action: Action,
        set: ((current: State) -> State) -> Unit,
        get: () -> State
    ): Effect<Action, Event>
}
