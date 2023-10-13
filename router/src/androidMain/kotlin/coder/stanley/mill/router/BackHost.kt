package coder.stanley.mill.router

import androidx.compose.runtime.Composable

@Composable
actual fun BackHost(content: @Composable () -> Unit) {
    // Nothing needed in Android
    content()
}
