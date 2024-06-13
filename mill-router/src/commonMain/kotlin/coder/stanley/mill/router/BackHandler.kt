package coder.stanley.mill.router

import androidx.compose.runtime.Composable

/**
 * A back handler can be used to intercept the back event triggered by user.
 *
 * @param isEnabled is this back handler enabled, if false it will pass the event to the upper level
 * @param onBack the function that will be invoked when back event triggered
 */
@Composable
expect fun BackHandler(isEnabled: Boolean = true, onBack: () -> Unit)
