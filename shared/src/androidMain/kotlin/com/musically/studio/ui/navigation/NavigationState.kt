package com.musically.studio.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import androidx.navigation3.runtime.NavKey

@Composable
fun rememberNavigationState(
    startRoute: Route,
    topLevelRoutes: Set<Route>,
    serializer: MutableStateSerializer<Route>? = null
): NavigationState {
    val topLevelRoute = if (serializer != null) {
        rememberSerializable(
            startRoute, topLevelRoutes,
            serializer = serializer
        ) {
            mutableStateOf(startRoute)
        }
    } else {
        remember(startRoute, topLevelRoutes) {
            mutableStateOf(startRoute)
        }
    }

    val backStacks: Map<Route, NavBackStack<NavKey>> = topLevelRoutes.associateWith { key: Route -> rememberNavBackStack(key) }

    val state = remember(topLevelRoutes) {
        NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks
        )
    }
    
    state.startRoute = startRoute
    return state
}

class NavigationState(
    var startRoute: Route,
    topLevelRoute: MutableState<Route>,
    val backStacks: Map<Route, NavBackStack<NavKey>>
) {
    var topLevelRoute: Route by topLevelRoute
    val stacksInUse: List<Route>
        get() = if (topLevelRoute == startRoute) {
            listOf(startRoute)
        } else {
            listOf(startRoute, topLevelRoute)
        }
}

@Composable
fun NavigationState.toEntries(
    entryProvider: (Route) -> NavEntry<Route>
): SnapshotStateList<NavEntry<Route>> {
    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<Route>(),
        )
        
        @Suppress("UNCHECKED_CAST")
        rememberDecoratedNavEntries(
            backStack = stack as NavBackStack<Route>,
            entryDecorators = decorators,
            entryProvider = entryProvider
        )
    }

    return stacksInUse
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}
