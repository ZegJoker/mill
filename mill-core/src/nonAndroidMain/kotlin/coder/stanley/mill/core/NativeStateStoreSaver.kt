package coder.stanley.mill.core

import androidx.compose.runtime.compositionLocalOf

actual class StateStoreSaver actual constructor() {

    private val map = mutableMapOf<String, StateStore<*, *, *>>()

    @Suppress("UNCHECKED_CAST")
    actual fun <Action, State, Event> getStore(key: String): StateStore<Action, State, Event>? {
        val store = map[key]
        if (store != null) {
            return store as StateStore<Action, State, Event>
        }
        return null
    }

    actual fun putStore(key: String, stateStore: StateStore<*, *, *>) {
        map.put(key, stateStore)?.onClear()
    }

    actual fun storeKeys(): Set<String> {
        return map.keys
    }

    actual fun clearStores() {
        map.forEach { (_, store) ->
            store.onClear()
        }
        map.clear()
    }
}

actual val LocalStateStoreSaver = compositionLocalOf {
    StateStoreSaver()
}
