package krio.systemdesign.shoppingapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hierarchy?.any {
        it.hasRoute<BottomNavRoutes.CatalogTab>() ||
            it.hasRoute<BottomNavRoutes.CartTab>()
    } == true

    if (!showBottomBar) return

    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = currentDestination.hierarchy.any {
                it.hasRoute<BottomNavRoutes.CatalogTab>()
            },
            onClick = { navController.navigateToBottomTab(BottomNavRoutes.CatalogTab) },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Каталог") },
        )
        NavigationBarItem(
            selected = currentDestination.hierarchy.any {
                it.hasRoute<BottomNavRoutes.CartTab>()
            },
            onClick = { navController.navigateToBottomTab(BottomNavRoutes.CartTab) },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
            label = { Text("Корзина") },
        )
    }
}

internal fun NavHostController.navigateToBottomTab(route: Any) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
