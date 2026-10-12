package krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import krio.systemdesign.shoppingapp.core.designsystem.components.cards.SectionCard
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ShoppingBag
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.crossfeature.domain.model.CartItem
import krio.systemdesign.shoppingapp.crossfeature.ui.text.formatPrice
import krio.systemdesign.shoppingapp.feature.checkout.impl.resources.Res
import krio.systemdesign.shoppingapp.feature.checkout.impl.resources.checkout_order_items
import krio.systemdesign.shoppingapp.feature.checkout.ui.OrderItemRow
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun OrderItemsSection(
    items: List<CartItem>,
    modifier: Modifier = Modifier,
) {
    SectionCard(
        icon = AppIcons.ShoppingBag,
        title = stringResource(Res.string.checkout_order_items),
        modifier = modifier,
    ) {
        items.forEach { item ->
            OrderItemRow(
                name = item.name,
                imageUrl = item.imageUrl,
                quantity = item.quantity,
                unitPrice = formatPrice(item.price),
                total = formatPrice(item.price * item.quantity),
            )
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = AndroidUiModes.UI_MODE_NIGHT_YES)
@Composable
private fun OrderItemsSectionPreview() {
    ShoppingAppTheme {
        Surface {
            OrderItemsSection(
                items = listOf(
                    CartItem("1", "Wireless Headphones", "", price = 14999, quantity = 1, availableQuantity = 10),
                    CartItem("2", "Mechanical Keyboard", "", price = 10995, quantity = 2, availableQuantity = 5),
                ),
                modifier = Modifier.padding(Spacing.ScreenPadding),
            )
        }
    }
}
