package coder.stanley.mill.core

import android.annotation.SuppressLint
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModelStore

actual class StateStoreSaver(private val viewModelStore: ViewModelStore = ViewModelStore()) {

    actual constructor() : this(ViewModelStore())

    @SuppressLint("RestrictedApi", "UNCHECKED_CAST")
    @Suppress("UNCHECKED_CAST")
    actual fun <Action, State, Event> getStore(key: String): StateStore<Action, State, Event>? {
        val store = viewModelStore[key]
        if (store != null) {
            return store as StateStore<Action, State, Event>
        }
        return null
    }

    @SuppressLint("RestrictedApi")
    actual fun putStore(key: String, stateStore: StateStore<*, *, *>) {
        viewModelStore.put(key, stateStore)
    }

    @SuppressLint("RestrictedApi")
    actual fun storeKeys(): Set<String> {
        return viewModelStore.keys()
    }

    actual fun clearStores() {
        viewModelStore.clear()
    }
}

actual val LocalStateStoreSaver = compositionLocalOf {
    StateStoreSaver()
}
