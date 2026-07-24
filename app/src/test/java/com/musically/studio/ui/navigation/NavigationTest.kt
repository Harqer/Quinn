package com.musically.studio.ui.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationTest {

    @Test
    fun `navigator navigate switches top level route`() {
        val startRoute: Route = Route.Home
        val topLevelRoutes = setOf<Route>(Route.Home, Route.Discover)
        val backStacks = topLevelRoutes.associateWith { NavBackStack(it) }
        val state = NavigationState(startRoute, mutableStateOf(startRoute), backStacks)
        val navigator = Navigator(state)

        navigator.navigate(Route.Discover)

        assertEquals(Route.Discover, state.topLevelRoute)
    }

    @Test
    fun `navigator navigate adds to current stack if not top level`() {
        val startRoute: Route = Route.Home
        val topLevelRoutes = setOf<Route>(Route.Home, Route.Discover)
        val backStacks = topLevelRoutes.associateWith { NavBackStack(it) }
        val state = NavigationState(startRoute, mutableStateOf(startRoute), backStacks)
        val navigator = Navigator(state)

        navigator.navigate(Route.Camera)

        assertEquals(Route.Camera, state.backStacks[Route.Home]?.last())
    }

    @Test
    fun `navigator goBack removes from current stack`() {
        val startRoute: Route = Route.Home
        val topLevelRoutes = setOf<Route>(Route.Home, Route.Discover)
        val backStacks = topLevelRoutes.associateWith { NavBackStack(it) }
        val state = NavigationState(startRoute, mutableStateOf(startRoute), backStacks)
        val navigator = Navigator(state)

        navigator.navigate(Route.Camera)
        navigator.goBack()

        assertEquals(Route.Home, state.backStacks[Route.Home]?.last())
    }

    @Test
    fun `navigator goBack from top level returns to start route`() {
        val startRoute: Route = Route.Home
        val topLevelRoutes = setOf<Route>(Route.Home, Route.Discover)
        val backStacks = topLevelRoutes.associateWith { NavBackStack(it) }
        val state = NavigationState(startRoute, mutableStateOf(Route.Discover), backStacks)
        val navigator = Navigator(state)

        navigator.goBack()

        assertEquals(Route.Home, state.topLevelRoute)
    }
}
