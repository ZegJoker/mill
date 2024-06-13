package coder.stanley.mill.router

import coder.stanley.mill.core.Effect
import coder.stanley.mill.core.Feature
import kotlin.reflect.KClass

internal class RouteFeature : Feature<RouteAction, RouteState, Unit>() {

    private fun PathRoute.tryMatchPath(given: String, prevContext: RouteContext?): RouteContext? {
        val matchResult = pattern.toRegex().matchEntire(given) ?: return null
        val mappedParam = mutableMapOf<String, Any?>()
        if (matchResult.groupValues.size - 1 != params.size) return null
        for (param in params) {
            mappedParam[param.name] =
                matchResult.groupValues[sortedParams.indexOf(param) + 1].toRouteParam(param.type)
        }
        return PathRouteContext(
            path = path,
            paramPath = given,
            params = mappedParam,
            prevContext = if (prevContext != null &&
                (prevContext !is TypedRouteContext ||
                        prevContext.target != RouteContext.InitialTarget)
            ) prevContext else null
        )
    }

    private fun RouteContext?.findPrev(path: String): RouteContext? {
        this ?: return null
        return if (this is PathRouteContext && this.path == path) this
        else prevContext.findPrev(path)
    }

    private fun TypedRoute.tryMatchTarget(given: Any, prevContext: RouteContext?): RouteContext? {
        if (given::class != this.targetClass) return null
        return TypedRouteContext(
            target = given,
            prevContext = if (prevContext != null &&
                (prevContext !is TypedRouteContext ||
                        prevContext.target != RouteContext.InitialTarget)
            ) prevContext else null
        )
    }

    private fun Route.tryMatch(given: Any, prevContext: RouteContext?): RouteContext? {
        return when {
            this is PathRoute && given is String -> {
                tryMatchPath(given, prevContext)
            }

            this is TypedRoute -> {
                tryMatchTarget(given, prevContext)
            }

            else -> null
        }
    }

    private fun RouteContext?.findPrev(target: KClass<*>): RouteContext? {
        this ?: return null
        return if (this is TypedRouteContext && this.target::class == target) this
        else prevContext.findPrev(target)
    }

    override fun FeatureBuilder<RouteAction, RouteState, Unit>.buildFeature() {
        Reducer { action, set, get ->
            when (action) {
                is RouteAction.SetGraph -> {
                    set { currentState ->
                        val route = if (
                            currentState.currentRoute is TypedRouteContext && currentState.currentRoute.target == RouteContext.InitialTarget
                        ) {
                            if (action.start is String) {
                                PathRouteContext(path = action.start)
                            } else {
                                TypedRouteContext(target = action.start)
                            }
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
                        val prev = currentRoute.prevContext
                        if (prev != null) {
                            currentState.copy(currentRoute = prev)
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
                    } else if (action.popUpToPath != null) {
                        currentRoute.findPrev(action.popUpToPath) ?: currentRoute
                    } else {
                        currentRoute
                    }
                    for (route in currentState.graph.routes) {
                        val context = route.tryMatch(action.target, prevRoute)
                        if (context != null) {
                            when(context) {
                                is TypedRouteContext -> {
                                    set { current ->
                                        current.copy(
                                            currentRoute = context.copy(
                                                index = currentRoute.index + 1,
                                                enterSpec = action.enterTransition
                                                    ?: route.enterTransitionSpec,
                                                exitSpec = action.exitTransition ?: route.exitTransitionSpec
                                            )
                                        )
                                    }
                                }
                                is PathRouteContext -> {
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
                    }
                }

                is RouteAction.PopUpTo -> {
                    set { currentState ->
                        val currentRoute = currentState.currentRoute
                        val route = if (action.target != null) {
                            currentRoute.findPrev(action.target) ?: currentRoute
                        } else if (action.path != null) {
                            currentRoute.findPrev(action.path) ?: currentRoute
                        } else {
                            throw IllegalArgumentException("Pop up param must be specified")
                        }
                        currentState.copy(currentRoute = route)
                    }
                }
            }
            Effect.none()
        }
    }
}
