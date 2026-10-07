package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import krio.systemdesign.shoppingapp.core.designsystem.components.cards.SectionCard
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Receipt
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.feature.cart.impl.R
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.CartUiState
import krio.systemdesign.shoppingapp.shared.ui.order.OrderTotals
import krio.systemdesign.shoppingapp.shared.ui.text.formatPrice

// The same card as the order total at checkout: the promo notice, a divider, then the totals.
@Composable
internal fun CartTotalsCard(
    promoCode: CartUiState.AppliedPromoCode?,
    totals: CartUiState.Totals,
    onRemovePromo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionCard(
        icon = AppIcons.Receipt,
        title = stringResource(R.string.cart_order_total),
        modifier = modifier,
    ) {
        if (promoCode != null) {
            CartPromoCodeNotice(promoCode = promoCode, onRemove = onRemovePromo)
            HorizontalDivider()
        }
        OrderTotals(
            subtotal = formatPrice(totals.subtotal),
            total = formatPrice(totals.total),
            discount = totals.discount?.let { formatPrice(it) },
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CartTotalsCardPreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(Spacing.ScreenPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.CardSpacing),
            ) {
                // No promo code; a valid one; an invalid one, which gives no discount.
                CartTotalsCard(
                    promoCode = null,
                    totals = CartUiState.Totals(subtotal = 25994, discount = null, total = 25994),
                    onRemovePromo = {},
                )
                CartTotalsCard(
                    promoCode = CartUiState.AppliedPromoCode("SALE10", discountPercent = 10, isValid = true),
                    totals = CartUiState.Totals(subtotal = 25994, discount = 2599, total = 23395),
                    onRemovePromo = {},
                )
                CartTotalsCard(
                    promoCode = CartUiState.AppliedPromoCode("SALE10", discountPercent = 10, isValid = false),
                    totals = CartUiState.Totals(subtotal = 25994, discount = null, total = 25994),
                    onRemovePromo = {},
                )
            }
        }
    }
}
