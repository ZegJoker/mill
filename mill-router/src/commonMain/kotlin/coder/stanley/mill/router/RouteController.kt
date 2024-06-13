package coder.stanley.mill.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coder.stanley.mill.core.StateStore
import coder.stanley.mill.core.rememberStore
import kotlin.reflect.KClass

/**
 * The route controller, which can be used to navigate to anew route or back to a specific route.
 */
class RouteController(internal val innerController: StateStore<RouteAction, RouteState, Unit>) {

    /**
     * Navigate back to the previous route.
     */
    fun navigateUp() {
        innerController.dispatch(RouteAction.Back)
    }

    /**
     * Navigate to the matched route.
     * When [popUpTo] or [popUpToPath] specified, the previous route will be replaced with the
     * matched route.
     */
    fun navigateTo(
        target: Any,
        popUpTo: KClass<*>? = null,
        popUpToPath: String? = null,
        enterTransitionSpec: RouteEnterTransition? = null,
        exitTransitionSpec: RouteExitTransition? = null,
    ) {
        innerController.dispatch(
            RouteAction.NavTo(
                target = target,
                popUpTo = popUpTo,
                popUpToPath = popUpToPath,
                enterTransition = enterTransitionSpec,
                exitTransition = exitTransitionSpec
            )
        )
    }

    /**
     * Pop up the route to a previous route which matches [target], if no previous route found then
     * nothing will happen.
     */
    fun popUpTo(target: KClass<*>) = innerController.dispatch(RouteAction.PopUpTo(target = target))

    /**
     * Pop up the route to a previous route which matches [path], if no previous route found then
     * nothing will happen.
     */
    fun popUpTo(path: String) = innerController.dispatch(RouteAction.PopUpTo(path = path))
}

/**
 * Create or get a [RouteController] in compose.
 */
@Composable
fun rememberRouteController(name: String = "mill-router"): RouteController {
    val store = rememberStore(
        feature = RouteFeature(),
        name = name,
        initialState = { RouteState() }
    )
    return remember(store) { RouteController(store) }
}
