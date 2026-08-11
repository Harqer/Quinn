package com.musically.studio.ui.navigation

/**
 * Handles navigation events (forward and back) by updating the navigation state.
 */
class Navigator(val state: NavigationState) {
    fun navigate(route: Route) {
        if (route in state.backStacks.keys) {
            // This is a top level route, just switch to it.
            state.topLevelRoute = route
        } else {
            val currentStack = state.backStacks[state.topLevelRoute]
            if (currentStack != null && currentStack.lastOrNull() != route) {
                currentStack.add(route)
            }
        }
    }

    fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute] ?: return
        if (currentStack.size > 1) {
            currentStack.removeLastOrNull()
        } else if (state.topLevelRoute != Route.Home) {
            state.topLevelRoute = Route.Home
        }
    }

    fun clearAll() {
        // Clear all backstacks except their root top-level route
        state.backStacks.forEach { (route, stack) ->
            stack.clear()
            stack.add(route)
        }
    }

    fun resetToRoute(route: Route) {
        clearAll()
        if (route in state.backStacks.keys) {
            state.topLevelRoute = route
        } else {
            state.topLevelRoute = Route.Welcome
        }
    }
}
