package krio.systemdesign.shoppingapp.navigation.bottombar

import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import krio.systemdesign.shoppingapp.R
import krio.systemdesign.shoppingapp.core.designsystem.components.bars.AppNavigationBarItem
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.HomeFilled
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.SettingsFilled
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ShoppingCartFilled

@Composable
fun AppBottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: AppBottomBarViewModel = hiltViewModel(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // No bottom bar outside tabs (checkout).
    val currentTab = navBackStackEntry?.destination?.bottomTab() ?: return

    val cartItemCount by viewModel.cartItemCount.collectAsStateWithLifecycle()

    NavigationBar(modifier = modifier) {
        AppNavigationBarItem(
            selected = currentTab.hasRoute<BottomNavRoutes.CatalogTab>(),
            onClick = { navController.navigateToBottomTab(BottomNavRoutes.CatalogTab) },
            icon = AppIcons.HomeFilled,
            label = stringResource(R.string.app_bottom_bar_catalog),
        )
        AppNavigationBarItem(
            selected = currentTab.hasRoute<BottomNavRoutes.CartTab>(),
            onClick = { navController.navigateToBottomTab(BottomNavRoutes.CartTab) },
            icon = AppIcons.ShoppingCartFilled,
            label = stringResource(R.string.app_bottom_bar_cart),
            badgeCount = cartItemCount,
        )
        AppNavigationBarItem(
            selected = currentTab.hasRoute<BottomNavRoutes.SettingsTab>(),
            onClick = { navController.navigateToBottomTab(BottomNavRoutes.SettingsTab) },
            icon = AppIcons.SettingsFilled,
            label = stringResource(R.string.app_bottom_bar_settings),
        )
    }
}
