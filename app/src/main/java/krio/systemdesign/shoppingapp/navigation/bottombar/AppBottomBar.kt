package krio.systemdesign.shoppingapp.navigation.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import krio.systemdesign.shoppingapp.R
import krio.systemdesign.shoppingapp.core.ui.components.bars.AppNavigationBarItem

@Composable
fun AppBottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: AppBottomBarViewModel = hiltViewModel(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hierarchy?.any {
        it.hasRoute<BottomNavRoutes.CatalogTab>() ||
            it.hasRoute<BottomNavRoutes.CartTab>() ||
            it.hasRoute<BottomNavRoutes.SettingsTab>()
    } == true

    if (!showBottomBar) return

    val cartItemCount by viewModel.cartItemCount.collectAsStateWithLifecycle()

    NavigationBar(modifier = modifier) {
        AppNavigationBarItem(
            selected = currentDestination.hierarchy.any {
                it.hasRoute<BottomNavRoutes.CatalogTab>()
            },
            onClick = { navController.navigateToBottomTab(BottomNavRoutes.CatalogTab) },
            icon = Icons.Default.Home,
            label = stringResource(R.string.app_bottom_bar_catalog),
        )
        AppNavigationBarItem(
            selected = currentDestination.hierarchy.any {
                it.hasRoute<BottomNavRoutes.CartTab>()
            },
            onClick = { navController.navigateToBottomTab(BottomNavRoutes.CartTab) },
            icon = Icons.Default.ShoppingCart,
            label = stringResource(R.string.app_bottom_bar_cart),
            badgeCount = cartItemCount,
        )
        AppNavigationBarItem(
            selected = currentDestination.hierarchy.any {
                it.hasRoute<BottomNavRoutes.SettingsTab>()
            },
            onClick = { navController.navigateToBottomTab(BottomNavRoutes.SettingsTab) },
            icon = Icons.Default.Settings,
            label = stringResource(R.string.app_bottom_bar_settings),
        )
    }
}

// Снимаем со стека всё, включая каталог: под открытой вкладкой ничего не лежит,
// поэтому «Назад» с корня любой вкладки выходит из приложения, а не ведёт в каталог.
internal fun NavHostController.navigateToBottomTab(route: Any) {
    navigate(route) {
        popUpTo(graph.id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
