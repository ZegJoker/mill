package coder.stanley.mill.sample

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coder.stanley.mill.core.NamedReducer
import coder.stanley.mill.core.reducer.createReducer
import coder.stanley.mill.core.rememberStore
import coder.stanley.mill.router.LocalRouteContext
import coder.stanley.mill.router.RouteHost
import coder.stanley.mill.router.RouteParam
import coder.stanley.mill.router.RouteParamType
import coder.stanley.mill.router.getIntParam
import coder.stanley.mill.router.navigateTo
import coder.stanley.mill.router.rememberRouteController
import coder.stanley.mill.router.route

@Composable
fun SampleContent(modifier: Modifier = Modifier) {
    val controller = rememberRouteController()

    RouteHost(
        controller = controller,
        modifier = modifier.fillMaxSize(),
        startRoute = "first",
        enterTransitionSpec = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Start)
        },
        exitTransitionSpec = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.End)
        }
    ) {
        route("first") {
            FirstPage {
                controller.navigateTo("second/$it")
            }
        }

        route(
            path = "second/{data}",
            params = listOf(
                RouteParam("data", type = RouteParamType.Int)
            )
        ) {
            SecondPage()
        }
    }
}

@Composable
fun FirstPage(modifier: Modifier = Modifier, navToSecond: (Int) -> Unit) {
    val counterReducer: NamedReducer<Unit, Int, Unit> = remember {
        createReducer("counter-reducer") { _, currentState, _ ->
            currentState + 1
        }
    }

    val currentRoute = LocalRouteContext.current

    val store = rememberStore(
        counterReducer,
        name = "store: ${currentRoute.id}"
    ) { 0 }

    val state by store.state.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Count: $state")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            store.dispatch(Unit)
        }) {
            Text("  + 1  ")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            navToSecond(state)
        }) {
            Text("To 2nd")
        }
    }
}

@Composable
fun SecondPage(
    modifier: Modifier = Modifier
) {
    val currentRoute = LocalRouteContext.current

    val param = currentRoute.getIntParam("data") ?: 0

    val counterReducer: NamedReducer<Unit, Int, Unit> = remember {
        createReducer("counter-reducer") { _, currentState, _ ->
            currentState + 1
        }
    }

    val store = rememberStore(
        counterReducer,
        name = "store: ${currentRoute.id}"
    ) { param }

    val state by store.state.collectAsState()

    Column(
        modifier = modifier.fillMaxSize().background(Color.Cyan),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Greeting from second, count: $state")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            store.dispatch(Unit)
        }) {
            Text("  + 1  ")
        }
    }
}


