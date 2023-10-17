# Mill

[![Build](https://github.com/ZegJoker/mill/actions/workflows/ci.yml/badge.svg)](https://github.com/ZegJoker/mill/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.zegjoker/mill-core)](https://search.maven.org/artifact/io.github.zegjoker/mill-core)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)

Mill is a tool for compose multiplatform developers to easily manage the UI states.

## Setup

### Adding Dependency
Before using **mill**, adding the dependency is needed
```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.zegjoker:mill-core:$version")
                implementation("io.github.zegjoker:mill-router:$version")
            }
        }
    }
}
```
If you only want to use the part for UI state management, you can only `core` to the `dependencies` block
```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.zegjoker:mill-core:$version")
            }
        }
    }
}
```

## Overview
If you are a fullstack developer, this will be easiest part for you because mill uses the almost the same strategy as Redux.
If you are new to Redux or **mill**, don't worry, the following guide will teach you steps by steps.
There are 5 important elements/components in mill:
- **ViewStateStore**: The place to store the UI states, provide an interface to pass the UI actions to the reducer, also provide an effect subscription
- **Reducer**: This is the place you can put the core logic in, it will process the pass-in UI action with the current state and produce a new state to store
- **ViewState**: Represents the UI state that UI view will show different element accordingly.
- **Action**: Any actions that comes from UI view, like click, input
- **Effect**: The stuff which are not related to UI state, such as navigation event, dialog pops

## Usage
### UI State Management
1. Provides the `ViewStateStoreSaver` at the root composable
```kotlin
// Android 
CompositionLocalProvider(
    LocalViewStateStoreSaver provides ViewStateStoreSaver(viewModelStore) // viewModelStore comes from activity
) {
    content() // Your UI content
}
// Other platforms
CompositionLocalProvider(LocalViewStateStoreSaver.provides(ViewStateStoreSaver())) {
    content() // Your UI content
}
```
2. Create the reducer by implements the interface or call the convenience function:
   1. Implement interface
   ``` kotlin
   class CounterReducer: NamedReducer<Unit, Int, Unit> {
       override val name: String = "CounterReducer"
       override suspend fun reduce(action: Unit, currentState: Int, onEffect: (Unit) -> Unit) {
           return currentState + 1
       }
   }
   ```
   2. Create from the convenience function
   ```kotlin
   val counterReducer = createReducer("counter-reducer") {_, current, _ ->
       current + 1
   }
   ```
3. Create a `ViewStateStore` by using `rememberStore` function, it will need you to pass 2/3 params:
- Reducer
- Name(Optional)
- function to initiate the state
```kotlin
val store = rememberStore(
    counterReducer,
) { 0 }
```
4. Subscribe the state in UI composable
```kotlin
val state by store.state.collectAsState()
```
Then you can use the state to show the UI.
```kotlin
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
}
```

### Navigation
Mill has a built-in navigation component named `router`, it is pretty much the same as android's navigation compose. To use it:
1. Create a route controller
```kotlin
val controller = rememberRouteControler
```
2. Create the route graph with `RouteHost`
```kotlin
RouteHost(
    controller = controller,
    modifier = Modifier.fillMaxSize(),
    startRoute = "start"
) {
    route(path ="start") {
        StartScreen(onNav = {
            controller.navigateTo("second/12")
        })
    }
    
    route(
        path = "second/{param}", 
        params = listOf(
            RouteParam("param", type = RouteParamType.Int)
        )
    ) {
        val param = LocalRouteContext.current.params["data"]?.toString()?.toInt() ?: 0
        SecondScreen(param = param, onBack = {
            controller.navigateUp()
        })
    }
}
```
## License

```
Copyright 2023 ZegJoker.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
   

