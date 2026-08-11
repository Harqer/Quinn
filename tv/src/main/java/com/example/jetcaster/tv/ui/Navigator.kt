package com.example.jetcaster.tv.ui

import androidx.navigation3.runtime.NavKey

/**
 * Handles navigation events by mutating the NavigationState.
 * All navigation in the TV app routes through this class.
 */
class Navigator(val state: NavigationState) {

    /** Navigate to a route. Top-level routes switch the active stack; others push onto it. */
    fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            state.topLevelRoute = route
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    /** Pop the current entry, or return to startRoute if at stack root. */
    fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()
        if (currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }
}
