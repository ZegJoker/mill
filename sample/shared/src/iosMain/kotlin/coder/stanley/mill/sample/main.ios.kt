package coder.stanley.mill.sample

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import coder.stanley.mill.core.LocalStateStoreSaver
import coder.stanley.mill.core.StateStoreSaver
import platform.UIKit.UIViewController

@Suppress("unused")
fun MainViewController(): UIViewController = ComposeUIViewController {
    CompositionLocalProvider(LocalStateStoreSaver.provides(StateStoreSaver())) {
        SampleContent()
    }
}
