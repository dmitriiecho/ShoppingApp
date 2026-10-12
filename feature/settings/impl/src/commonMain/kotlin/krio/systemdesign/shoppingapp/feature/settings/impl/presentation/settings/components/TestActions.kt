package krio.systemdesign.shoppingapp.feature.settings.impl.presentation.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import krio.systemdesign.shoppingapp.core.designsystem.components.cards.AppListItem
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Inventory2
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Link
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.OpenInNew
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ProductionQuantityLimits
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Sell
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Warning
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.Res
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_add_not_enough_stock_product
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_add_not_enough_stock_product_description
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_add_price_changed_not_enough_stock_product
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_add_price_changed_not_enough_stock_product_description
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_add_price_changed_product
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_add_price_changed_product_description
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_add_unavailable_product
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_add_unavailable_product_description
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_deep_links_page
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_deep_links_page_description
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TestActions(
    onDeepLinksPageClick: () -> Unit,
    onAddUnavailableProductClick: () -> Unit,
    onAddNotEnoughStockProductClick: () -> Unit,
    onAddPriceChangedProductClick: () -> Unit,
    onAddPriceChangedNotEnoughStockProductClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        AppListItem(
            icon = AppIcons.Link,
            title = stringResource(Res.string.settings_deep_links_page),
            description = stringResource(Res.string.settings_deep_links_page_description),
            onClick = onDeepLinksPageClick,
            trailing = { Icon(AppIcons.OpenInNew, contentDescription = null) },
        )
        // These three icons match the cart notices the added products cause.
        AppListItem(
            icon = AppIcons.Inventory2,
            title = stringResource(Res.string.settings_add_unavailable_product),
            description = stringResource(Res.string.settings_add_unavailable_product_description),
            onClick = onAddUnavailableProductClick,
        )
        AppListItem(
            icon = AppIcons.ProductionQuantityLimits,
            title = stringResource(Res.string.settings_add_not_enough_stock_product),
            description = stringResource(Res.string.settings_add_not_enough_stock_product_description),
            onClick = onAddNotEnoughStockProductClick,
        )
        AppListItem(
            icon = AppIcons.Sell,
            title = stringResource(Res.string.settings_add_price_changed_product),
            description = stringResource(Res.string.settings_add_price_changed_product_description),
            onClick = onAddPriceChangedProductClick,
        )
        AppListItem(
            // The cart has no notice for this combination, so a general warning icon.
            icon = AppIcons.Warning,
            title = stringResource(Res.string.settings_add_price_changed_not_enough_stock_product),
            description = stringResource(Res.string.settings_add_price_changed_not_enough_stock_product_description),
            onClick = onAddPriceChangedNotEnoughStockProductClick,
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = AndroidUiModes.UI_MODE_NIGHT_YES)
@Composable
private fun TestActionsPreview() {
    ShoppingAppTheme {
        Surface {
            TestActions(
                onDeepLinksPageClick = {},
                onAddUnavailableProductClick = {},
                onAddNotEnoughStockProductClick = {},
                onAddPriceChangedProductClick = {},
                onAddPriceChangedNotEnoughStockProductClick = {},
            )
        }
    }
}
