package coder.stanley.mill.core

import androidx.compose.runtime.compositionLocalOf

actual class ViewStateStoreSaver actual constructor() {

    private val map = mutableMapOf<String, ViewStateStore<*, *, *>>()

    actual fun <Action, State, Effect> getStore(key: String): ViewStateStore<Action, State, Effect>? {
        val store = map[key]
        if (store != null) {
            return store as ViewStateStore<Action, State, Effect>
        }
        return null
    }

    actual fun putStore(key: String, viewStateStore: ViewStateStore<*, *, *>) {
        map.put(key, viewStateStore)?.onClear()
    }

    actual fun storeKeys(): Set<String> {
        return map.keys
    }

    actual fun clearStores() {
        map.forEach {(_, store) ->
            store.onClear()
        }
        map.clear()
    }
}

actual val LocalViewStateStoreSaver = compositionLocalOf {
    ViewStateStoreSaver()
}
