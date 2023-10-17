package coder.stanley.mill.router

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
actual fun BackHost(content: @Composable () -> Unit) {
    var delta = 0f
    var backStart = false
    var attemptToBack by remember { mutableStateOf(false) }
    val draggableState = rememberDraggableState {
        delta += it
    }

    LaunchedEffect(attemptToBack) {
        if (attemptToBack) {

            var realBackHandle = backHandlers.lastOrNull()
            for (back in backHandlers.reversed()) {
                if (back.isEnabled) {
                    realBackHandle = back
                    break
                }
            }
            realBackHandle?.onBack?.invoke()
            attemptToBack = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().draggable(
        state = draggableState,
        orientation = Orientation.Horizontal,
        onDragStarted = {
            if (it.x <= 80) backStart = true
        },
        onDragStopped = {
            if (backStart && delta >= 120) attemptToBack = true
            backStart = false
            delta = 0f
        }
    ))

    content()
}

class BackHandle(val isEnabled: Boolean = true, val onBack: () -> Unit)

val backHandlers = mutableListOf<BackHandle>()
