package krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout.components

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
import krio.systemdesign.shoppingapp.feature.checkout.impl.R
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode
import krio.systemdesign.shoppingapp.shared.ui.order.OrderTotals
import krio.systemdesign.shoppingapp.shared.ui.promo.AppliedPromoCodeNotice
import krio.systemdesign.shoppingapp.shared.ui.text.formatPrice

@Composable
internal fun OrderTotalSection(
    subtotal: Long,
    discount: Long,
    total: Long,
    promoCode: PromoCode?,
    modifier: Modifier = Modifier,
) {
    SectionCard(
        icon = AppIcons.Receipt,
        title = stringResource(R.string.checkout_order_total),
        modifier = modifier,
    ) {
        // Same as in the cart: the promo notice, a divider, then the totals.
        if (promoCode != null) {
            AppliedPromoCodeNotice(
                code = promoCode.code,
                discountPercent = promoCode.discountPercent,
            )
            HorizontalDivider()
        }
        OrderTotals(
            subtotal = formatPrice(subtotal),
            total = formatPrice(total),
            discount = if (promoCode != null) formatPrice(discount) else null,
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OrderTotalSectionPreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(Spacing.ScreenPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.SectionSpacing),
            ) {
                OrderTotalSection(subtotal = 25994, discount = 0, total = 25994, promoCode = null)
                OrderTotalSection(subtotal = 25994, discount = 2599, total = 23395, promoCode = PromoCode("SALE10", 10))
            }
        }
    }
}
