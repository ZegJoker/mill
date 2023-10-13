package coder.stanley.mill.router

import androidx.compose.runtime.Composable
import coder.stanley.mill.core.ViewStateStore
import coder.stanley.mill.core.rememberStore

typealias RouteController = ViewStateStore<RouteAction, RouteContext, Unit>

fun RouteController.navigateUp() = dispatch(RouteAction.Back)

fun RouteController.navigateTo(
    path: String,
    popUpTo: String? = null,
    enterTransitionSpec: RouteEnterTransition? = null,
    exitTransitionSpec: RouteExitTransition? = null,
) = dispatch(
    RouteAction.NavTo(
        path = path,
        popUpTo = popUpTo,
        enterTransition = enterTransitionSpec,
        exitTransition = exitTransitionSpec
    )
)

fun RouteController.popUpTo(path: String) = dispatch(RouteAction.PopUpTo(path = path))

@Composable
fun rememberRouteController(): RouteController {
    return rememberStore(
        reducer = RouteReducer(),
        initialState = {
            RouteContext.INITIAL
        }
    )
}
