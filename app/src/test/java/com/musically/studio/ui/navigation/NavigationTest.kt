package com.musically.studio.ui.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationTest {

    private object TestRoute1 : NavKey
    private object TestRoute2 : NavKey
    private object SubRoute : NavKey

    @Test
    fun `navigator navigate switches top level route`() {
        val startRoute = TestRoute1
        val topLevelRoutes = setOf(TestRoute1, TestRoute2)
        val backStacks = topLevelRoutes.associateWith { NavBackStack(it) }
        val state = NavigationState(startRoute, mutableStateOf(startRoute), backStacks)
        val navigator = Navigator(state)

        navigator.navigate(TestRoute2)

        assertEquals(TestRoute2, state.topLevelRoute)
    }

    @Test
    fun `navigator navigate adds to current stack if not top level`() {
        val startRoute = TestRoute1
        val topLevelRoutes = setOf(TestRoute1, TestRoute2)
        val backStacks = topLevelRoutes.associateWith { NavBackStack(it) }
        val state = NavigationState(startRoute, mutableStateOf(startRoute), backStacks)
        val navigator = Navigator(state)

        navigator.navigate(SubRoute)

        assertEquals(SubRoute, state.backStacks[TestRoute1]?.last())
    }

    @Test
    fun `navigator goBack removes from current stack`() {
        val startRoute = TestRoute1
        val topLevelRoutes = setOf(TestRoute1, TestRoute2)
        val backStacks = topLevelRoutes.associateWith { NavBackStack(it) }
        val state = NavigationState(startRoute, mutableStateOf(startRoute), backStacks)
        val navigator = Navigator(state)

        navigator.navigate(SubRoute)
        navigator.goBack()

        assertEquals(TestRoute1, state.backStacks[TestRoute1]?.last())
    }

    @Test
    fun `navigator goBack from top level returns to start route`() {
        val startRoute = TestRoute1
        val topLevelRoutes = setOf(TestRoute1, TestRoute2)
        val backStacks = topLevelRoutes.associateWith { NavBackStack(it) }
        val state = NavigationState(startRoute, mutableStateOf(TestRoute2), backStacks)
        val navigator = Navigator(state)

        navigator.goBack()

        assertEquals(TestRoute1, state.topLevelRoute)
    }
}
