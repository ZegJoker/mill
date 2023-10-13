package coder.stanley.mill.router

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.compositionLocalOf
import coder.stanley.mill.core.ViewStateStoreSaver

data class RouteContext(
    val path: String,
    val paramPath: String = path,
    val params: Map<String, Any?> = emptyMap(),
    val prevContext: RouteContext? = null,
    val index: Int = 0,
    val enterSpec : RouteEnterTransition = { fadeIn() },
    val exitSpec: RouteExitTransition = { fadeOut() }
) {

    val id = uuid()

    val viewModelSaver = ViewStateStoreSaver()

    companion object {
        val INITIAL = RouteContext(path = "", params = emptyMap())
    }
}

val LocalRouteContext = compositionLocalOf { RouteContext.INITIAL }

typealias RouteEnterTransition = AnimatedContentTransitionScope<RouteContext>.() -> EnterTransition
typealias RouteExitTransition = AnimatedContentTransitionScope<RouteContext>.() -> ExitTransition
