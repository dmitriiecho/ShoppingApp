package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Inventory2
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ProductionQuantityLimits
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Sell
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.feature.cart.impl.R
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.CartUiState
import krio.systemdesign.shoppingapp.feature.cart.ui.CartChangeNotice
import krio.systemdesign.shoppingapp.shared.ui.order.TotalBottomBar
import krio.systemdesign.shoppingapp.shared.ui.text.formatPrice

@Composable
internal fun CartBottomBar(
    total: Long,
    changes: CartUiState.Changes,
    canCheckout: Boolean,
    isOpeningCheckout: Boolean,
    onCheckout: () -> Unit,
    onAcceptNewPrices: () -> Unit,
    onRemoveUnavailable: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TotalBottomBar(
        total = formatPrice(total),
        actionText = stringResource(R.string.cart_checkout),
        enabled = canCheckout,
        isLoading = isOpeningCheckout,
        onAction = onCheckout,
        modifier = modifier,
        header = if (changes.hasAny) {
            {
                CartChanges(
                    changes = changes,
                    onAcceptNewPrices = onAcceptNewPrices,
                    onRemoveUnavailable = onRemoveUnavailable,
                )
            }
        } else {
            null
        },
    )
}

// A notice per kind of unfixed change: how many items it touches and a button that fixes them all.
// Missing stock has no button: the user lowers each quantity with "−".
@Composable
private fun CartChanges(
    changes: CartUiState.Changes,
    onAcceptNewPrices: () -> Unit,
    onRemoveUnavailable: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val priceChangeCount = changes.priceChangeCount
        if (priceChangeCount > 0) {
            CartChangeNotice(
                icon = AppIcons.Sell,
                title = pluralStringResource(R.plurals.cart_price_changes, priceChangeCount, priceChangeCount),
                actionText = stringResource(R.string.cart_accept_new_prices),
                onAction = onAcceptNewPrices,
            )
        }
        val unavailableCount = changes.unavailableItemCount
        if (unavailableCount > 0) {
            CartChangeNotice(
                icon = AppIcons.Inventory2,
                title = pluralStringResource(R.plurals.cart_unavailable_items, unavailableCount, unavailableCount),
                actionText = stringResource(R.string.cart_remove_unavailable),
                onAction = onRemoveUnavailable,
            )
        }
        val notEnoughStockCount = changes.notEnoughStockItemCount
        if (notEnoughStockCount > 0) {
            CartChangeNotice(
                icon = AppIcons.ProductionQuantityLimits,
                title = pluralStringResource(
                    R.plurals.cart_not_enough_stock_items,
                    notEnoughStockCount,
                    notEnoughStockCount,
                ),
            )
        }
    }
}

private val NoChanges = CartUiState.Changes(priceChangeCount = 0, unavailableItemCount = 0, notEnoughStockItemCount = 0)

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CartBottomBarPreview() {
    ShoppingAppTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Ready to check out; waiting for the validation before checkout opens.
            CartBottomBar(
                total = 23395,
                changes = NoChanges,
                canCheckout = true,
                isOpeningCheckout = false,
                onCheckout = {},
                onAcceptNewPrices = {},
                onRemoveUnavailable = {},
            )
            CartBottomBar(
                total = 23395,
                changes = NoChanges,
                canCheckout = false,
                isOpeningCheckout = true,
                onCheckout = {},
                onAcceptNewPrices = {},
                onRemoveUnavailable = {},
            )
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CartBottomBarWithChangesPreview() {
    ShoppingAppTheme {
        CartBottomBar(
            total = 17449,
            changes = CartUiState.Changes(priceChangeCount = 1, unavailableItemCount = 1, notEnoughStockItemCount = 1),
            canCheckout = false,
            isOpeningCheckout = false,
            onCheckout = {},
            onAcceptNewPrices = {},
            onRemoveUnavailable = {},
        )
    }
}
