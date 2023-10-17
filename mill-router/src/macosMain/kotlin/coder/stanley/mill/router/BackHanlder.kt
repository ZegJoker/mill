package coder.stanley.mill.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

@Composable
actual fun BackHandler(isEnabled: Boolean, onBack: () -> Unit) {}
