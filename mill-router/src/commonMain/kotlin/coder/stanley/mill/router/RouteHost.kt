package coder.stanley.mill.router

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coder.stanley.mill.core.LocalStateStoreSaver

/**
 * Provides in place in the Compose hierarchy for self contained navigation to occur.
 *
 * Once this is called, any Composable within the given [RouteGraph.Builder] can be navigated to from
 * the provided [controller].
 *
 * The builder passed into this method is [remember]ed. This means that for this NavHost, the
 * contents of the builder cannot be changed.
 *
 * @param controller the navController for this host
 * @param startRoute the route for the start destination
 * @param modifier The modifier to be applied to the layout.
 * @param contentAlignment The [Alignment] of the [AnimatedContent]
 * @param enterTransitionSpec callback to define enter transitions for destination in this host
 * @param exitTransitionSpec callback to define exit transitions for destination in this host
 * @param routes the builder used to construct the graph
 */
@Composable
fun RouteHost(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    controller: RouteController,
    startRoute: Any,
    enterTransitionSpec: RouteEnterTransition = { fadeIn(animationSpec = tween(350)) },
    exitTransitionSpec: RouteExitTransition = { fadeOut(animationSpec = tween(350)) },
    routes: RouteGraph.Builder.() -> Unit,
) {
    val graph = remember(startRoute, routes) {
        RouteGraph.Builder()
            .setEnterAnim(enterTransitionSpec)
            .setExitAnim(exitTransitionSpec)
            .apply(routes)
            .build()
    }
    val state by controller.innerController.state.collectAsState()

    LaunchedEffect(controller, graph) {
        controller.innerController.dispatch(RouteAction.SetGraph(graph, start = startRoute))
    }

    val saveableStateHolder = rememberSaveableStateHolder()

    BackHost {
        BackHandler(isEnabled = state.currentRoute.prevContext != null) {
            controller.navigateUp()
        }

        val transition = updateTransition(state.currentRoute, label = "route")

        transition.AnimatedContent(
            modifier = modifier,
            transitionSpec = {
                if (initialState.prevContext == null && targetState.prevContext == null) {
                    EnterTransition.None togetherWith ExitTransition.None
                } else {
                    if (targetState.index < initialState.index) {
                        EnterTransition.None.togetherWith(initialState.exitSpec(this)).also {
                            it.targetContentZIndex = targetState.index.toFloat()
                        }
                    } else {
                        targetState.enterSpec(this).togetherWith(ExitTransition.None).also {
                            it.targetContentZIndex = targetState.index.toFloat()
                        }
                    }
                }
            },
            contentAlignment = contentAlignment,
            contentKey = { it.id }
        ) {
            CompositionLocalProvider(
                LocalRouteContext.provides(it),
                LocalStateStoreSaver provides it.viewModelSaver,
                LocalSaveableStateRegistry provides it.saveableStateRegistry
            ) {
                for (route in graph.routes) {
                    if (
                        route is TypedRoute
                        && it is TypedRouteContext
                        && route.targetClass == it.target::class
                    ) {
                        saveableStateHolder.SaveableStateProvider(it.id, route.content)
                    } else if (
                        route is PathRoute
                        && it is PathRouteContext
                        && route.path == it.path
                    ) {
                        saveableStateHolder.SaveableStateProvider(it.id, route.content)
                    }
                }
            }
        }
    }
}
