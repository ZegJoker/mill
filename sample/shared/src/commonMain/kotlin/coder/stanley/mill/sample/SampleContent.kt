package coder.stanley.mill.sample

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coder.stanley.mill.core.Effect
import coder.stanley.mill.core.Feature
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
            SecondPage{
                controller.navigateTo("third/$it")
            }
        }

        route(
            path = "third/{data}",
            params = listOf(
                RouteParam("data", type = RouteParamType.Int)
            )
        ) {
            ThirdPage()
        }
    }
}

@Composable
fun Title(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium)
}

class CounterFeature: Feature<Unit, Int, Unit>() {
    override fun FeatureBuilder<Unit, Int, Unit>.buildFeature() {
        Reducer { _, set, _ ->
            set { it + 1 }
            Effect.none()
        }
    }
}

@Composable
fun GeneralPage(
    modifier: Modifier,
    pageName: String,
    nextPageName: String? = null,
    onNavToNextPage: ((Int) -> Unit)? = null
) {
    val counterFeature = remember {
        CounterFeature()
    }

    val currentRoute = LocalRouteContext.current
    val param = currentRoute.getIntParam("data") ?: 0

    val store = rememberStore(
        name = "store: ${currentRoute.id}",
        counterFeature
    ) { param }

    val state by store.state.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Title("$pageName Page")

        Text("Count: $state")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            store.dispatch(Unit)
        }) {
            Text("  + 1  ")
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (nextPageName != null && onNavToNextPage != null) {
            Button(onClick = {
                onNavToNextPage(state)
            }) {
                Text("To $nextPageName")
            }
        }
    }
}

@Composable
fun FirstPage(modifier: Modifier = Modifier, navToSecond: (Int) -> Unit) {
    GeneralPage(modifier, pageName = "1st", nextPageName = "2nd", onNavToNextPage = navToSecond)
}

@Composable
fun SecondPage(
    modifier: Modifier = Modifier,
    onNavTo3rd: ((Int) -> Unit)?
) {
    GeneralPage(modifier, pageName = "2nd", nextPageName = "3rd", onNavToNextPage = onNavTo3rd)
}

@Composable
fun ThirdPage(
    modifier: Modifier = Modifier,
) {
    GeneralPage(modifier, pageName = "3rd")
}


