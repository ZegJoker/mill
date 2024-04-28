package coder.stanley.mill.router

import coder.stanley.mill.core.NamedReducer


class RouteReducer : NamedReducer<RouteAction, RouteContext, Unit> {

    private var routeGraph: RouteGraph = RouteGraph(emptyList())

    override suspend fun reduce(
        action: RouteAction,
        currentState: RouteContext,
        onEffect: (Unit) -> Unit,
    ): RouteContext {
        when (action) {
            is RouteAction.SetGraph -> {
                routeGraph = action.routeGraph
                return if (currentState.path == RouteContext.INITIAL.path) RouteContext(path = action.startPath)
                else currentState
            }

            is RouteAction.Back -> return currentState.prevContext ?: currentState

            is RouteAction.NavTo -> {
                val prevContext = if (action.popUpTo != null) {
                    currentState.findPrev(action.popUpTo) ?: currentState
                } else {
                    currentState
                }
                for (route in routeGraph.routes) {
                    val context = route.tryMatch(action.path, prevContext)
                    if (context != null) return context.copy(
                        index = currentState.index + 1,
                        enterSpec = action.enterTransition ?: route.enterTransitionSpec,
                        exitSpec = action.exitTransition ?: route.exitTransitionSpec
                    )
                }
                return currentState
            }

            is RouteAction.PopUpTo -> {
                return currentState.findPrev(action.path) ?: currentState
            }
        }
    }

    private fun Route.tryMatch(given: String, prevContext: RouteContext?): RouteContext? {
        val matchResult = pattern.toRegex().matchEntire(given) ?: return null
        val mappedParam = mutableMapOf<String, Any?>()
        if (matchResult.groupValues.size - 1 != params.size) return null
        for (param in params) {
            mappedParam[param.name] =
                matchResult.groupValues[sortedParams.indexOf(param) + 1].toRouteParam(param.type)
        }
        return RouteContext(
            path = path,
            paramPath = given,
            params = mappedParam,
            prevContext = if (!prevContext?.path.isNullOrBlank()) prevContext else null
        )
    }

    private fun RouteContext?.findPrev(path: String): RouteContext? {
        this ?: return null
        return if (this.path == path) this
        else prevContext.findPrev(path)
    }

    override val name: String = "Stanley-mpp-route-reducer"
}
