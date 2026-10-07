package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.Notice
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.NoticeStyle
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Inventory2
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ProductionQuantityLimits
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Sell
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.feature.cart.impl.R
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.CartUiState
import krio.systemdesign.shoppingapp.shared.ui.cart.CartQuantityControl
import krio.systemdesign.shoppingapp.shared.ui.product.ProductCard
import krio.systemdesign.shoppingapp.shared.ui.product.ProductImageKey
import krio.systemdesign.shoppingapp.shared.ui.text.formatPrice

// The callbacks take the item or its id instead of capturing it in the list: then the list passes the same
// lambdas to every card on each change, and a card whose item didn't change is skipped.
@Composable
internal fun CartItemCard(
    item: CartUiState.Item,
    onClick: (CartUiState.Item) -> Unit,
    onQuantityChange: (productId: String, quantity: Int) -> Unit,
    onRemove: (productId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val onIncrease = { onQuantityChange(item.productId, item.quantity + 1) }
    ProductCard(
        name = item.name,
        imageUrl = item.imageUrl,
        price = formatPrice(item.price * item.quantity),
        onClick = { onClick(item) },
        modifier = modifier,
        unitPrice = formatPrice(item.price),
        sharedElementKey = ProductImageKey(item.productId),
        isDimmed = CartUiState.Issue.Unavailable in item.issues,
    ) {
        if (item.issues.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item.issues.forEach { ItemIssueNotice(issue = it) }
            }
        }
        CartQuantityControl(
            quantity = item.quantity,
            onAdd = onIncrease,
            onIncrease = onIncrease,
            onDecrease = { onQuantityChange(item.productId, item.quantity - 1) },
            onRemoveAll = { onRemove(item.productId) },
            modifier = Modifier.fillMaxWidth(),
            // The cart holds all the stock or the item is sold out: it can only go down or be removed.
            canIncrease = item.canAddOneMore,
        )
    }
}

// All red: any of these changes blocks checkout until the user fixes it. They differ in icon and text.
@Composable
private fun ItemIssueNotice(
    issue: CartUiState.Issue,
    modifier: Modifier = Modifier,
) {
    when (issue) {
        CartUiState.Issue.Unavailable -> Notice(
            icon = AppIcons.Inventory2,
            title = stringResource(R.string.cart_item_unavailable),
            style = NoticeStyle.Error,
            modifier = modifier,
        )
        is CartUiState.Issue.PriceChanged -> Notice(
            icon = AppIcons.Sell,
            // The old price is right above in the card, so the notice shows only the new one.
            title = stringResource(R.string.cart_item_price_changed, formatPrice(issue.newPrice)),
            style = NoticeStyle.Error,
            modifier = modifier,
        )
        is CartUiState.Issue.NotEnoughStock -> Notice(
            icon = AppIcons.ProductionQuantityLimits,
            title = stringResource(R.string.cart_item_not_enough_stock, issue.availableQuantity),
            style = NoticeStyle.Error,
            modifier = modifier,
        )
    }
}

private fun previewItem(
    productId: String,
    name: String,
    price: Long,
    quantity: Int,
    issues: ImmutableList<CartUiState.Issue> = persistentListOf(),
    canAddOneMore: Boolean = true,
) = CartUiState.Item(
    productId = productId,
    name = name,
    imageUrl = "",
    price = price,
    quantity = quantity,
    issues = issues,
    canAddOneMore = canAddOneMore,
)

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CartItemCardPreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(Spacing.ScreenPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.CardSpacing),
            ) {
                // No issues; sold out, so dimmed and without "+"; a new price and not enough stock at once.
                CartItemCard(
                    item = previewItem(productId = "1", name = "Wireless Headphones", price = 14999, quantity = 2),
                    onClick = {},
                    onQuantityChange = { _, _ -> },
                    onRemove = {},
                )
                CartItemCard(
                    item = previewItem(
                        productId = "16",
                        name = "Hoodie",
                        price = 4499,
                        quantity = 1,
                        issues = persistentListOf(CartUiState.Issue.Unavailable),
                        canAddOneMore = false,
                    ),
                    onClick = {},
                    onQuantityChange = { _, _ -> },
                    onRemove = {},
                )
                CartItemCard(
                    item = previewItem(
                        productId = "32",
                        name = "Notebook",
                        price = 995,
                        quantity = 10,
                        issues = persistentListOf(
                            CartUiState.Issue.PriceChanged(newPrice = 1295),
                            CartUiState.Issue.NotEnoughStock(availableQuantity = 5),
                        ),
                        canAddOneMore = false,
                    ),
                    onClick = {},
                    onQuantityChange = { _, _ -> },
                    onRemove = {},
                )
            }
        }
    }
}
