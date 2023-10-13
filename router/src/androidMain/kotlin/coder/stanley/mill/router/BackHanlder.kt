package coder.stanley.mill.router

import androidx.activity.compose.BackHandler as AndroidBackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(isEnabled: Boolean, onBack: () -> Unit) =
    AndroidBackHandler(isEnabled, onBack)
