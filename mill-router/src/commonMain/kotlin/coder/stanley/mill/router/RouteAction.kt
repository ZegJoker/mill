package coder.stanley.mill.router

import kotlin.reflect.KClass

/**
 * [RouteAction] describes how actions that can be used by route controller.
 */
sealed class RouteAction {

    /**
     * Navigate back to the previous route.
     */
    internal data object Back : RouteAction()

    /**
     * Navigate to the matched route.
     * When [popUpTo] or [popUpToPath] specified, the previous route will be replaced with the
     * matched route.
     */
    internal data class NavTo(
        val target: Any,
        val popUpTo: KClass<*>? = null,
        val popUpToPath: String? = null,
        val enterTransition: RouteEnterTransition? = null,
        val exitTransition: RouteExitTransition? = null,
    ) : RouteAction()

    /**
     * Pop up the route to the matched route.
     *
     * @throws IllegalArgumentException if [target] and [path] are both null
     */
    internal data class PopUpTo(
        val target: KClass<*>? = null,
        val path: String? = null,
    ) : RouteAction()

    /**
     * Set the route graph with a specified start route.
     */
    internal data class SetGraph(
        val routeGraph: RouteGraph,
        val start: Any
    ) : RouteAction()
}
