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
import coder.stanley.mill.router.getRouteParam
import coder.stanley.mill.router.rememberRouteController
import coder.stanley.mill.router.route
import coder.stanley.mill.router.getRouteTarget
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

@Composable
fun SampleContent(modifier: Modifier = Modifier) {
    val controller = rememberRouteController()

    RouteHost(
        controller = controller,
        modifier = modifier.fillMaxSize(),
        startRoute = First,
        enterTransitionSpec = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Start)
        },
        exitTransitionSpec = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.End)
        }
    ) {
        route<First> {
            FirstPage {
                controller.navigateTo("second/$it")
            }
        }

        route(
            path = "second/{data}",
            params = listOf(
                RouteParam("data", type = RouteParamType.Int)
            )
        )  {
            SecondPage {
                controller.navigateTo(Third(it))
            }
        }

        route<Third> {
            ThirdPage()
        }
    }
}

interface NumberPage {
    val data: Int
}

object First : NumberPage {
    override val data: Int = 0
}

data class Third(override val data: Int) : NumberPage

@Composable
fun Title(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium)
}

sealed class CounterAction {
    data object Add : CounterAction()

    data object Tick: CounterAction()
}

class CounterFeature : Feature<CounterAction, Int, Unit>() {
    override fun FeatureBuilder<CounterAction, Int, Unit>.buildFeature() {
        Reducer { action, set, _ ->
            when (action) {
                is CounterAction.Add -> {
                    set { it + 1 }
                    Effect.none()
                }
                is CounterAction.Tick -> {
                    Effect.task("tick-tick") { send ->
                        while(coroutineContext.isActive) {
                            delay(1000)
                            send(CounterAction.Add)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GeneralPage(
    modifier: Modifier,
    pageName: String,
    initialNumber: Int,
    id: String,
    nextPageName: String? = null,
    onNavToNextPage: ((Int) -> Unit)? = null
) {
    val counterFeature = remember {
        CounterFeature()
    }

    val store = rememberStore(
        name = "store: $id",
        counterFeature
    ) { initialNumber }

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
            store.dispatch(CounterAction.Add)
        }) {
            Text("  + 1  ")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            store.dispatch(CounterAction.Tick)
        }) {
            Text("  Tick  ")
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

    val context = LocalRouteContext.current
    GeneralPage(
        modifier,
        pageName = "1st",
        initialNumber = 0,
        id = context.id,
        nextPageName = "2nd",
        onNavToNextPage = navToSecond
    )
}

@Composable
fun SecondPage(
    modifier: Modifier = Modifier,
    onNavTo3rd: ((Int) -> Unit)?
) {
    val context = LocalRouteContext.current
    val param = getRouteParam<Int>("data")
    GeneralPage(
        modifier,
        pageName = "2nd",
        initialNumber = param,
        id = context.id,
        nextPageName = "3rd",
        onNavToNextPage = onNavTo3rd
    )
}

@Composable
fun ThirdPage(
    modifier: Modifier = Modifier,
) {
    val context = LocalRouteContext.current
    val param = getRouteTarget<Third>().data
    GeneralPage(modifier, initialNumber = param, id = context.id, pageName = "3rd")
}


