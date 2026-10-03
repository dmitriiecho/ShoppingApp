package krio.systemdesign.shoppingapp.uikit.sections

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.components.bars.AppNavigationBarItem
import krio.systemdesign.shoppingapp.core.ui.components.bars.TotalBottomBar
import krio.systemdesign.shoppingapp.core.ui.components.notices.Notice
import krio.systemdesign.shoppingapp.core.ui.components.notices.NoticeStyle
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.text.formatPrice
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.components.SampleData
import krio.systemdesign.shoppingapp.uikit.components.SampleList
import krio.systemdesign.shoppingapp.uikit.components.SampleVariant
import krio.systemdesign.shoppingapp.uikit.components.sampleGroup
import kotlinx.coroutines.delay

@Composable
fun BarsSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("TotalBottomBar") {
            SampleVariant(stringResource(R.string.uikit_variant_tap_to_load)) {
                var isLoading by rememberSaveable { mutableStateOf(false) }
                LaunchedEffect(isLoading) {
                    if (isLoading) {
                        delay(SampleData.LOADING_MILLIS)
                        isLoading = false
                    }
                }
                TotalBottomBar(
                    total = formatPrice(SampleData.HEADPHONES_PRICE),
                    actionText = stringResource(R.string.uikit_sample_checkout),
                    enabled = !isLoading,
                    isLoading = isLoading,
                    onAction = { isLoading = true },
                )
            }
            SampleVariant(stringResource(R.string.uikit_variant_with_header)) {
                TotalBottomBar(
                    total = formatPrice(SampleData.HEADPHONES_PRICE),
                    actionText = stringResource(R.string.uikit_sample_place_order),
                    enabled = true,
                    isLoading = false,
                    onAction = {},
                    header = {
                        Notice(
                            icon = AppIcons.Info,
                            title = stringResource(R.string.uikit_sample_demo_order),
                            style = NoticeStyle.Neutral,
                        )
                    },
                )
            }
        }
        // Панели во всю ширину рамки: так они стоят и в приложении — от края до края экрана.
        sampleGroup("NavigationBar · AppNavigationBarItem") {
            SampleVariant(stringResource(R.string.uikit_variant_badge), contentPadding = 0.dp) {
                SampleNavigationBar(cartItemCount = 3)
            }
            SampleVariant(stringResource(R.string.uikit_variant_badge_overflow), contentPadding = 0.dp) {
                SampleNavigationBar(cartItemCount = 120)
            }
        }
    }
}

// Нижняя панель как в приложении: те же вкладки и значки. Вкладки переключаются.
@Composable
private fun SampleNavigationBar(cartItemCount: Int) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    NavigationBar {
        AppNavigationBarItem(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            icon = Icons.Default.Home,
            label = stringResource(R.string.uikit_sample_tab_catalog),
        )
        AppNavigationBarItem(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            icon = Icons.Default.ShoppingCart,
            label = stringResource(R.string.uikit_sample_tab_cart),
            badgeCount = cartItemCount,
        )
        AppNavigationBarItem(
            selected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            icon = Icons.Default.Settings,
            label = stringResource(R.string.uikit_sample_tab_settings),
        )
    }
}
