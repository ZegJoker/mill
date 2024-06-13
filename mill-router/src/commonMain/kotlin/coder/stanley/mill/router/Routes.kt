package coder.stanley.mill.router

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlin.reflect.KClass

enum class RouteParamType {
    String,
    Boolean,
    Int,
    Long,
    Double,
    Float
}

fun String.toRouteParam(type: RouteParamType): Any? {
    if ("null" == this) return null
    return when (type) {
        RouteParamType.String -> this
        RouteParamType.Boolean -> toBooleanStrictOrNull()
        RouteParamType.Int -> toIntOrNull()
        RouteParamType.Long -> toLongOrNull()
        RouteParamType.Float -> toFloatOrNull()
        RouteParamType.Double -> toDoubleOrNull()
    }
}

data class RouteParam(
    val name: String,
    val type: RouteParamType,
)

sealed class Route(
    val enterTransitionSpec: RouteEnterTransition = { fadeIn() },
    val exitTransitionSpec: RouteExitTransition = { fadeOut() },
    val content: @Composable () -> Unit,
)

class PathRoute(
    val path: String,
    val params: List<RouteParam>,
    enterTransitionSpec: RouteEnterTransition = { fadeIn() },
    exitTransitionSpec: RouteExitTransition = { fadeOut() },
    content: @Composable () -> Unit,
): Route(enterTransitionSpec, exitTransitionSpec, content) {
    val sortedParams : List<RouteParam>
        get() {
            val paramInPath = mutableMapOf<Int, RouteParam>()
            for (param in params) {
                val index = path.indexOf("{${param.name}}")
                paramInPath[index] = param
            }
            return buildList {
                for (key in paramInPath.keys.sorted()) {
                    paramInPath[key]?.let {
                        add(it)
                    }
                }
            }
        }

    val pattern: String
        get() {
            var pattern = path
            for (param in params) {
                pattern = pattern.replace("{${param.name}}", "(.+)")
            }
            return pattern
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PathRoute

        return path == other.path
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }
}

class TypedRoute(
    val targetClass: KClass<*>,
    enterTransitionSpec: RouteEnterTransition = { fadeIn() },
    exitTransitionSpec: RouteExitTransition = { fadeOut() },
    content: @Composable () -> Unit,
): Route(enterTransitionSpec, exitTransitionSpec, content) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as TypedRoute
        return targetClass == other.targetClass
    }

    override fun hashCode(): Int {
        return targetClass.hashCode()
    }
}

data class RouteGraph(
    val routes: List<Route>,
) {
    class Builder {
        private val routes = mutableListOf<Route>()
        var enterAnim: RouteEnterTransition = { fadeIn() }
            private set
        var exitAnim: RouteExitTransition = { fadeOut() }
            private set

        fun addRoute(route: Route): Builder {
            routes.add(route)
            return this
        }

        fun setEnterAnim(enterAnim: RouteEnterTransition): Builder {
            this.enterAnim = enterAnim
            return this
        }

        fun setExitAnim(exitAnim: RouteExitTransition): Builder {
            this.exitAnim = exitAnim
            return this
        }

        fun build(): RouteGraph {
            return RouteGraph(routes)
        }
    }
}

/**
 * Create a type safe route and added to the graph.
 */
inline fun <reified T> RouteGraph.Builder.route(
    noinline enterTransitionSpec: RouteEnterTransition = enterAnim,
    noinline exitTransitionSpec: RouteExitTransition = exitAnim,
    crossinline content: @Composable () -> Unit,
) {
    addRoute(
        TypedRoute(
            T::class,
            content = {
              Box(modifier = Modifier.fillMaxSize()) {
                  content()
              }
            },
            enterTransitionSpec = enterTransitionSpec,
            exitTransitionSpec = exitTransitionSpec
        )
    )
}

/**
 * Create a path route and added to the graph.
 */
inline fun RouteGraph.Builder.route(
    path: String,
    params: List<RouteParam> = emptyList(),
    noinline enterTransitionSpec: RouteEnterTransition = enterAnim,
    noinline exitTransitionSpec: RouteExitTransition = exitAnim,
    crossinline content: @Composable () -> Unit,
) {
    addRoute(
        PathRoute(
            path = path,
            params = params,
            content = {
                Box(modifier = Modifier.fillMaxSize()) {
                    content()
                }
            },
            enterTransitionSpec = enterTransitionSpec,
            exitTransitionSpec = exitTransitionSpec,
        )
    )
}

