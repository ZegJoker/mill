package coder.stanley.mill.core

sealed class Effect<Action, Event> {

    companion object {
        fun <Action, Event> none(): Effect<Action, Event> = None()

        fun <Action, Event> event(event: Event): Effect<Action, Event> = EventEmitter(event)

        fun <Action, Event> task(
            id: String,
            execute: suspend (send: (Action) -> Unit) -> Unit
        ): Effect<Action, Event> = Task(id, execute)

        fun <Action, Event> cancel(id: String): Effect<Action, Event> = CancelTask(id)
    }

    internal class None<Action, Event> : Effect<Action, Event>()

    internal class Task<Action, Event>(
        val id: String,
        val execute: suspend (send: (Action) -> Unit) -> Unit
    ) : Effect<Action, Event>()

    internal class EventEmitter<Action, Event>(val event: Event): Effect<Action, Event>()

    internal class CancelTask<Action, Event>(val id: String) : Effect<Action, Event>()

    internal class ListEffect<Action, Event>(val effects: List<Effect<Action, Event>>) :
        Effect<Action, Event>()
}
