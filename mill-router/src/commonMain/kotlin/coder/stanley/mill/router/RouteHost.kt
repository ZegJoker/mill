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
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coder.stanley.mill.core.LocalViewStateStoreSaver

@Composable
fun RouteHost(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    controller: RouteController,
    startRoute: String,
    enterTransitionSpec: RouteEnterTransition = { fadeIn(animationSpec = tween(350)) },
    exitTransitionSpec: RouteExitTransition = { fadeOut(animationSpec = tween(350)) },
    routes: RouteGraph.Builder.() -> Unit,
) {
    val graph = RouteGraph.Builder()
        .setEnterAnim(enterTransitionSpec)
        .setExitAnim(exitTransitionSpec)
        .apply(routes)
        .build()
    val state by controller.state.collectAsState()

    LaunchedEffect(controller) {
        controller.dispatch(RouteAction.SetGraph(graph, startPath = startRoute))
    }

    val saveableStateHolder = rememberSaveableStateHolder()

    BackHost {
        BackHandler(isEnabled = state.prevContext != null) {
            controller.navigateUp()
        }

        val transition = updateTransition(state, label = "route")

        transition.AnimatedContent(
            modifier = modifier,
            transitionSpec = {
                if (initialState.prevContext == null && targetState.prevContext == null) {
                    EnterTransition.None togetherWith ExitTransition.None
                } else {
                    targetState.enterSpec(this).togetherWith(initialState.exitSpec(this)).also {
                        it.targetContentZIndex = targetState.index.toFloat()
                    }
                }
            },
            contentAlignment = contentAlignment,
            contentKey = { it.id }
        ) {
            CompositionLocalProvider(
                LocalRouteContext.provides(it),
                LocalViewStateStoreSaver provides it.viewModelSaver,
                LocalSaveableStateRegistry provides it.saveableStateRegistry
            ) {
                for (route in graph.routes) {
                    if (route.path == it.path) {
                        saveableStateHolder.SaveableStateProvider(it.id, route.content)
                    }
                }
            }
        }
    }
}
