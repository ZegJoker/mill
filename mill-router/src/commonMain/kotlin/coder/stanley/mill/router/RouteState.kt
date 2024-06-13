package coder.stanley.mill.router

/**
 * Route state.
 */
data class RouteState(
    val graph: RouteGraph = RouteGraph(emptyList()),
    val currentRoute: RouteContext = RouteContext.INITIAL
)
