package coder.stanley.mill.router

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable

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

class Route(
    val path: String,
    val params: List<RouteParam>,
    val enterTransitionSpec: RouteEnterTransition = { fadeIn() },
    val exitTransitionSpec: RouteExitTransition = { fadeOut() },
    val content: @Composable () -> Unit,
) {

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

        other as Route

        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        return path.hashCode()
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

fun RouteGraph.Builder.route(
    path: String,
    params: List<RouteParam> = emptyList(),
    enterTransitionSpec: RouteEnterTransition? = null,
    exitTransitionSpec: RouteExitTransition? = null,
    content: @Composable () -> Unit,
) {
    addRoute(
        Route(
            path = path,
            params = params,
            content = content,
            enterTransitionSpec = enterTransitionSpec ?: enterAnim,
            exitTransitionSpec = exitTransitionSpec ?: exitAnim
        )
    )
}

