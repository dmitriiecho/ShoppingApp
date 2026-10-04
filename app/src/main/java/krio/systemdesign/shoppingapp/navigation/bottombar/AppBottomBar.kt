package krio.systemdesign.shoppingapp.navigation.bottombar

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
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.HomeFilled
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.SettingsFilled
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ShoppingCartFilled

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
            icon = AppIcons.HomeFilled,
            label = stringResource(R.string.app_bottom_bar_catalog),
        )
        AppNavigationBarItem(
            selected = currentDestination.hierarchy.any {
                it.hasRoute<BottomNavRoutes.CartTab>()
            },
            onClick = { navController.navigateToBottomTab(BottomNavRoutes.CartTab) },
            icon = AppIcons.ShoppingCartFilled,
            label = stringResource(R.string.app_bottom_bar_cart),
            badgeCount = cartItemCount,
        )
        AppNavigationBarItem(
            selected = currentDestination.hierarchy.any {
                it.hasRoute<BottomNavRoutes.SettingsTab>()
            },
            onClick = { navController.navigateToBottomTab(BottomNavRoutes.SettingsTab) },
            icon = AppIcons.SettingsFilled,
            label = stringResource(R.string.app_bottom_bar_settings),
        )
    }
}

// Pops everything, the catalog too: Back from any tab root leaves the app instead of going to the catalog.
internal fun NavHostController.navigateToBottomTab(route: Any) {
    navigate(route) {
        popUpTo(graph.id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
