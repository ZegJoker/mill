package coder.stanley.mill.core

import androidx.compose.runtime.ProvidableCompositionLocal

expect class ViewStateStoreSaver constructor() {

    fun <Action, State, Effect> getStore(key: String): ViewStateStore<Action, State, Effect>?

    fun putStore(key: String, viewStateStore: ViewStateStore<*, *, *>)

    fun storeKeys(): Set<String>

    fun clearStores()
}


expect val LocalViewStateStoreSaver: ProvidableCompositionLocal<ViewStateStoreSaver>

