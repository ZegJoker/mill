package coder.stanley.mill.sample

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStore
import coder.stanley.mill.core.LocalViewStateStoreSaver
import coder.stanley.mill.core.ViewStateStoreSaver

@Composable
fun MainView(viewModelStore: ViewModelStore) {
    CompositionLocalProvider(
        LocalViewStateStoreSaver provides ViewStateStoreSaver(viewModelStore)
    ) {
        MaterialTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                SampleContent()
            }
        }
    }
}
