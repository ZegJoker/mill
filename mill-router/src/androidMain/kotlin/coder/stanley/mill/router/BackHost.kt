package coder.stanley.mill.router

import androidx.compose.runtime.Composable

@Composable
internal actual fun BackHost(content: @Composable () -> Unit) {
    // Nothing needed in Android
    content()
}
