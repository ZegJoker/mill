package coder.stanley.mill.router

import coder.stanley.mill.core.Effect
import coder.stanley.mill.core.Feature

internal class RouteFeature : Feature<RouteAction, RouteState, Unit>() {

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

    override fun FeatureBuilder<RouteAction, RouteState, Unit>.buildFeature() {
        Reducer { action, set, get ->
            when (action) {
                is RouteAction.SetGraph -> {
                    set { currentState ->
                        val route = if (currentState.currentRoute.path == RouteContext.INITIAL.path) {
                            RouteContext(path = action.startPath)
                        } else {
                            currentState.currentRoute
                        }
                        RouteState(
                            graph = action.routeGraph,
                            currentRoute = route
                        )
                    }
                }

                is RouteAction.Back -> {
                    set { currentState ->
                        val currentRoute = currentState.currentRoute
                        if (currentRoute.prevContext != null) {
                            currentState.copy(currentRoute = currentRoute.prevContext)
                        } else {
                            currentState
                        }
                    }
                }

                is RouteAction.NavTo -> {
                    val currentState = get()
                    val currentRoute = currentState.currentRoute
                    val prevRoute = if (action.popUpTo != null) {
                        currentRoute.findPrev(action.popUpTo) ?: currentRoute
                    } else {
                        currentRoute
                    }
                    for (route in currentState.graph.routes) {
                        val context = route.tryMatch(action.path, prevRoute)
                        if (context != null) {
                            set { current ->
                                current.copy(
                                    currentRoute = context.copy(
                                        index = currentRoute.index + 1,
                                        enterSpec = action.enterTransition ?: route.enterTransitionSpec,
                                        exitSpec = action.exitTransition ?: route.exitTransitionSpec
                                    )
                                )
                            }
                        }
                    }
                }

                is RouteAction.PopUpTo -> {
                    set { currentState ->
                        val currentRoute = currentState.currentRoute
                        val route = currentRoute.findPrev(action.path) ?: currentRoute
                        currentState.copy(currentRoute = route)
                    }
                }
            }
            Effect.none()
        }
    }
}
