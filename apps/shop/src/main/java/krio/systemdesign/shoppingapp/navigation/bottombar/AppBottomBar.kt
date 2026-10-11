package krio.systemdesign.shoppingapp.navigation.bottombar

import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.R
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.injectedViewModel
import krio.systemdesign.shoppingapp.core.designsystem.components.bars.AppNavigationBarItem
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.HomeFilled
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.SettingsFilled
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ShoppingCartFilled

@Composable
fun AppBottomBar(
    currentTab: BottomTab,
    onTabClick: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AppBottomBarViewModel = injectedViewModel(),
) {
    val cartItemCount by viewModel.cartItemCount.collectAsStateWithLifecycle()

    NavigationBar(modifier = modifier) {
        AppNavigationBarItem(
            selected = currentTab == BottomTab.Catalog,
            onClick = { onTabClick(BottomTab.Catalog) },
            icon = AppIcons.HomeFilled,
            label = stringResource(R.string.app_bottom_bar_catalog),
        )
        AppNavigationBarItem(
            selected = currentTab == BottomTab.Cart,
            onClick = { onTabClick(BottomTab.Cart) },
            icon = AppIcons.ShoppingCartFilled,
            label = stringResource(R.string.app_bottom_bar_cart),
            badgeCount = cartItemCount,
        )
        AppNavigationBarItem(
            selected = currentTab == BottomTab.Settings,
            onClick = { onTabClick(BottomTab.Settings) },
            icon = AppIcons.SettingsFilled,
            label = stringResource(R.string.app_bottom_bar_settings),
        )
    }
}
