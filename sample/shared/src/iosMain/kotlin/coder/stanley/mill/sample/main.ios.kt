package coder.stanley.mill.sample

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import coder.stanley.mill.core.LocalViewStateStoreSaver
import coder.stanley.mill.core.ViewStateStoreSaver
import platform.UIKit.UIViewController

@Suppress("unused")
fun MainViewController(): UIViewController = ComposeUIViewController {
    CompositionLocalProvider(LocalViewStateStoreSaver.provides(ViewStateStoreSaver())) {
        SampleContent()
    }
}
