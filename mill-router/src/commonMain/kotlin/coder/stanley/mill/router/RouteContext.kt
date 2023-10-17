package coder.stanley.mill.router

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.saveable.SaveableStateRegistry
import coder.stanley.mill.core.ViewStateStoreSaver

data class RouteContext(
    val path: String,
    val paramPath: String = path,
    val params: Map<String, Any?> = emptyMap(),
    val prevContext: RouteContext? = null,
    val index: Int = 0,
    val enterSpec: RouteEnterTransition = { fadeIn() },
    val exitSpec: RouteExitTransition = { fadeOut() }
) {

    val id = uuid()

    val viewModelSaver = ViewStateStoreSaver()

    val saveableStateRegistry = SaveableStateRegistry(emptyMap()) { true }

    companion object {
        val INITIAL = RouteContext(path = "", params = emptyMap())
    }
}

fun RouteContext.getIntParam(key: String): Int? {
    return params[key]?.toString()?.toIntOrNull()
}

fun RouteContext.getStringParam(key: String): String? {
    return params[key]?.toString()
}

fun RouteContext.getLongParam(key: String): Long? {
    return params[key]?.toString()?.toLongOrNull()
}

fun RouteContext.getBooleanParam(key: String): Boolean? {
    return params[key]?.toString()?.toBooleanStrictOrNull()
}

fun RouteContext.getFloatParam(key: String): Float? {
    return params[key]?.toString()?.toFloatOrNull()
}

fun RouteContext.getDoubleParam(key: String): Double? {
    return params[key]?.toString()?.toDoubleOrNull()
}

val LocalRouteContext = compositionLocalOf { RouteContext.INITIAL }

typealias RouteEnterTransition = AnimatedContentTransitionScope<RouteContext>.() -> EnterTransition
typealias RouteExitTransition = AnimatedContentTransitionScope<RouteContext>.() -> ExitTransition
