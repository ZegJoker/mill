package coder.stanley.mill.sample

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStore
import coder.stanley.mill.core.LocalStateStoreSaver
import coder.stanley.mill.core.StateStoreSaver

@Composable
fun MainView(viewModelStore: ViewModelStore) {
    CompositionLocalProvider(
        LocalStateStoreSaver provides StateStoreSaver(viewModelStore)
    ) {
        MaterialTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                SampleContent()
            }
        }
    }
}
