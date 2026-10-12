package krio.systemdesign.shoppingapp.navigation.bottombar

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

// The route of the app's root graph, the one that holds the tabs.
@Serializable
data object AppRoute

object BottomNavRoutes {
    @Serializable
    data object CatalogTab

    @Serializable
    data object CartTab

    @Serializable
    data object SettingsTab

    val all = listOf(CatalogTab, CartTab, SettingsTab)
}

// The tab graph the screen is in; null outside tabs (checkout).
internal fun NavDestination.bottomTab(): NavDestination? =
    hierarchy.firstOrNull { node -> BottomNavRoutes.all.any { node.hasRoute(it::class) } }

// Pops everything, the catalog too: Back from any tab root leaves the app instead of going to the catalog.
internal fun NavHostController.navigateToBottomTab(route: Any) {
    navigate(route) {
        popUpTo<AppRoute> {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
