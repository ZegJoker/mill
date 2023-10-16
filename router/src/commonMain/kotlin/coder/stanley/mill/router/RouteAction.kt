package coder.stanley.mill.router

sealed class RouteAction {

    data object Back : RouteAction()

    data class NavTo(
        val path: String,
        val popUpTo: String? = null,
        val enterTransition: RouteEnterTransition? = null,
        val exitTransition: RouteExitTransition? = null,
    ) : RouteAction()

    data class PopUpTo(
        val path: String
    ) : RouteAction()

    data class SetGraph(
        val routeGraph: RouteGraph,
        val startPath: String
    ) : RouteAction()
}
