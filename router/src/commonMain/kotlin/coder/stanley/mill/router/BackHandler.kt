package coder.stanley.mill.router

import androidx.compose.runtime.Composable

@Composable
expect fun BackHandler(isEnabled: Boolean = true, onBack: () -> Unit)
