package coder.stanley.mill.core

import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModelStore

actual class ViewStateStoreSaver(private val viewModelStore: ViewModelStore = ViewModelStore()) {

    actual constructor(): this(ViewModelStore())

    actual fun <Action, State, Effect> getStore(key: String): ViewStateStore<Action, State, Effect>? {
        val store = viewModelStore[key]
        if (store != null) {
            return store as ViewStateStore<Action, State, Effect>
        }
        return null
    }

    actual fun putStore(key: String, viewStateStore: ViewStateStore<*, *, *>) {
        viewModelStore.put(key, viewStateStore)
    }

    actual fun storeKeys(): Set<String> {
        return viewModelStore.keys()
    }

    actual fun clearStores() {
        viewModelStore.clear()
    }
}

actual val LocalViewStateStoreSaver = compositionLocalOf {
    ViewStateStoreSaver()
}
