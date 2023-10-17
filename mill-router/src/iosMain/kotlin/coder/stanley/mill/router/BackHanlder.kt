package coder.stanley.mill.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

@Composable
actual fun BackHandler(isEnabled: Boolean, onBack: () -> Unit) {
    val rememberedOnBack = remember {
        BackHandle(isEnabled, onBack)
    }
    DisposableEffect(Unit) {
        backHandlers.add(rememberedOnBack)
        onDispose {
            backHandlers.remove(rememberedOnBack)
        }
    }
}
