package coder.stanley.mill.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import kotlinx.coroutines.sync.Mutex
import kotlin.coroutines.CoroutineContext

actual open class StateStore<Action, State, Event>(
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

    private val jobCancellable = mutableMapOf<String, Boolean>()

    private val runningJobs = mutableMapOf<String, Job>()

    private val jobLock = Mutex()

    actual fun dispatch(action: Action) {
        handleEffect(feature.reduce(action, ::updateState, ::getState))
    }

    private fun runTask(taskId: String, cancellable: Boolean, block: suspend (send: (Action) -> Unit) -> Unit) {
        launch {
            jobLock.lock()
            val job = runningJobs[taskId]
            if (job != null && job.isActive && !job.isCompleted) {
                println("Mill - Warning - A task with id[$taskId] has already been started")
                if (jobCancellable[taskId] != false) {
                    job.cancel()
                }
            }
            val newJob = Job()
            runningJobs[taskId] = newJob
            jobCancellable[taskId] = cancellable
            jobLock.unlock()
            launch(newJob) {
                block {
                    dispatch(it)
                }
            }
        }
    }

    private fun cancelTask(taskId: String) {
        launch {
            jobLock.lock()
            runningJobs.remove(taskId)?.cancel()
            jobLock.unlock()
        }
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

            is Effect.Task -> {
                runTask(effect.id, effect.cancellable, effect.execute)
            }

            is Effect.CancelTask -> {
                cancelTask(effect.id)
            }

            is Effect.EventEmitter -> {
                _event.tryEmit(effect.event)
            }
        }
    }

    private fun updateState(newState: (State) -> State) {
        _state.value = newState(_state.value)
    }

    private fun getState() = _state.value

    actual open fun onClear() {
        launch {
            jobLock.lock()
            val jobs = buildList { addAll(runningJobs.entries) }
            for ((taskId, job) in jobs) {
                if (job.isActive && !job.isCompleted) {
                    if (jobCancellable[taskId] != false) {
                        job.cancel()
                    }
                }
            }
            runningJobs.clear()
            jobLock.unlock()
        }
    }
}

@Composable
actual fun <Action, State, Event> rememberStore(
    name: String,
    feature: Feature<Action, State, Event>,
    initialState: () -> State,
): StateStore<Action, State, Event> {
    val storeSaver = LocalStateStoreSaver.current
    val store = remember {
        storeSaver.getStore(name) ?: StateStore(
            feature,
            initialState
        ).also { storeSaver.putStore(name, it) }
    }
    DisposableEffect(store) {
        onDispose {
            store.onClear()
        }
    }
    return store
}

