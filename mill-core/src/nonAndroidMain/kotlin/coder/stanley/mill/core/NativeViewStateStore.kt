package coder.stanley.mill.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

actual open class ViewStateStore<Action, State, Event>(
    private val feature: Feature<Action, State, Event>,
    initialState: () -> State,
) : CoroutineScope {
    private val _state by lazy { MutableStateFlow(initialState()) }
    actual val state: StateFlow<State> by lazy { _state }

    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()

    private val _event = MutableSharedFlow<Event>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    actual val event: SharedFlow<Event> = _event

    private val runningJobs = mutableMapOf<String, Job>()

    actual fun dispatch(action: Action) {
        handleEffect(feature.reduce(action, ::updateState, ::getState))
    }

    private fun runTask(taskId: String, block: suspend (send: (Action) -> Unit) -> Unit) {
        val job = runningJobs[taskId]
        if (job != null && job.isActive && !job.isCompleted) {
            println("Mill - Warning - A task with id[$taskId] has already been started")
            job.cancel()
        }
        val newJob = Job()
        runningJobs[taskId] = newJob
        launch(newJob) {
            block {
                dispatch(it)
            }
        }
    }

    private fun cancelTask(taskId: String) {
        runningJobs.remove(taskId)?.cancel()

    }

    private fun flattenEffects(list: Effect.ListEffect<Action, Event>): List<Effect<Action, Event>> {
        val effects = mutableListOf<Effect<Action, Event>>()
        list.effects.forEach {
            when (it) {
                is Effect.None -> {}
                is Effect.ListEffect -> effects.addAll(flattenEffects(it))
                else -> effects.add(it)
            }
        }
        return effects
    }

    private fun handleEffect(effect: Effect<Action, Event>) {
        when (effect) {
            is Effect.None -> {}
            is Effect.ListEffect -> {
                flattenEffects(effect).forEach { handleEffect(it) }
            }
            is Effect.Task -> { runTask(effect.id, effect.execute) }
            is Effect.CancelTask -> { cancelTask(effect.id) }
            is Effect.EventEmitter ->  { _event.tryEmit(effect.event) }
        }
    }

    private fun updateState(newState: (State) -> State) {
        _state.value = newState(_state.value)
    }

    private fun getState() = _state.value

    actual open fun onClear() {
    }
}

@Composable
actual fun <Action, State, Event> rememberStore(
    name: String,
    feature: Feature<Action, State, Event>,
    initialState: () -> State,
): ViewStateStore<Action, State, Event> {
    val storeSaver = LocalViewStateStoreSaver.current
    return remember {
        storeSaver.getStore(name) ?: ViewStateStore(
            feature,
            initialState
        ).also { storeSaver.putStore(name, it) }
    }
}

