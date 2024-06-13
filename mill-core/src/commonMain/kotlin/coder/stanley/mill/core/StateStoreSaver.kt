package coder.stanley.mill.core

import androidx.compose.runtime.ProvidableCompositionLocal

expect class StateStoreSaver constructor() {

    fun <Action, State, Event> getStore(key: String): StateStore<Action, State, Event>?

    fun putStore(key: String, stateStore: StateStore<*, *, *>)

    fun storeKeys(): Set<String>

    fun clearStores()
}


expect val LocalStateStoreSaver: ProvidableCompositionLocal<StateStoreSaver>

