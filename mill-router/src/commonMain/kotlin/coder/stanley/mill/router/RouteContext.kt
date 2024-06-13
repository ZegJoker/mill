package coder.stanley.mill.router

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.saveable.SaveableStateRegistry
import coder.stanley.mill.core.StateStoreSaver

/**
 * [RouteContext] contains all information related to route.
 */
sealed class RouteContext(
    open val prevContext: RouteContext? = null,
    internal open val index: Int = 0,
    internal open val enterSpec: RouteEnterTransition,
    internal open val exitSpec: RouteExitTransition,
) {
    val id = uuid()

    internal val viewModelSaver = StateStoreSaver()

    internal val saveableStateRegistry = SaveableStateRegistry(emptyMap()) { true }

    companion object {
        internal val INITIAL = TypedRouteContext(target = InitialTarget)
    }

    internal object InitialTarget
}

internal data class PathRouteContext(
    val path: String,
    val paramPath: String = path,
    val params: Map<String, Any?> = emptyMap(),
    override val prevContext: RouteContext? = null,
    override val index: Int = 0,
    override val enterSpec: RouteEnterTransition = { fadeIn() },
    override val exitSpec: RouteExitTransition = { fadeOut() }
) : RouteContext(prevContext, index, enterSpec, exitSpec)

internal data class TypedRouteContext(
    internal val target: Any,
    override val prevContext: RouteContext? = null,
    override val index: Int = 0,
    override val enterSpec: RouteEnterTransition = { fadeIn() },
    override val exitSpec: RouteExitTransition = { fadeOut() }
) : RouteContext(prevContext, index, enterSpec, exitSpec)

/**
 * Get the route target from [RouteContext].
 */
@Suppress("UNCHECKED_CAST")
fun <T> getRouteTargetFromContext(context: RouteContext): T {
    return (context as TypedRouteContext).target as T
}

/**
 * Get a route param from [RouteContext].
 */
@Suppress("UNCHECKED_CAST")
fun <T> getRouteParamFromContext(key: String, context: RouteContext): T {
    return (context as PathRouteContext).params[key] as T
}

/**
 * Get the route target from the current [RouteContext].
 */
@Composable
fun <T> getRouteTarget(): T {
    val context = LocalRouteContext.current
    return getRouteTargetFromContext((context))
}

/**
 * Get a route param from the current [RouteContext].
 */
@Composable
fun <T> getRouteParam(key: String): T {
    val context = LocalRouteContext.current
    return getRouteParamFromContext(key, context)
}

/**
 * [LocalRouteContext] stores the current [RouteContext].
 */
val LocalRouteContext = compositionLocalOf<RouteContext> { RouteContext.INITIAL }

typealias RouteEnterTransition = AnimatedContentTransitionScope<RouteContext>.() -> EnterTransition
typealias RouteExitTransition = AnimatedContentTransitionScope<RouteContext>.() -> ExitTransition
