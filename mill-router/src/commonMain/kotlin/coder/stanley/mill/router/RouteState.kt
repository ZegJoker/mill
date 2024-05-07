package coder.stanley.mill.router

data class RouteState(
    val graph: RouteGraph = RouteGraph(emptyList()),
    val currentRoute: RouteContext = RouteContext.INITIAL
)
