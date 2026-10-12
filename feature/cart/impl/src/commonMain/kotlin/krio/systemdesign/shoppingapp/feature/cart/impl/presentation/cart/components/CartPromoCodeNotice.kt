package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.NoticeStyle
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.NoticeWithAction
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Error
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.crossfeature.ui.promo.AppliedPromoCodeNotice
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.CartUiState
import krio.systemdesign.shoppingapp.feature.cart.impl.resources.Res
import krio.systemdesign.shoppingapp.feature.cart.impl.resources.cart_promo_invalid
import krio.systemdesign.shoppingapp.feature.cart.impl.resources.cart_promo_invalid_hint
import krio.systemdesign.shoppingapp.feature.cart.impl.resources.cart_promo_remove
import org.jetbrains.compose.resources.stringResource

// Green while the code works; red, with a hint, once the cart validation finds it no longer does.
// Both have a Remove button.
@Composable
internal fun CartPromoCodeNotice(
    promoCode: CartUiState.AppliedPromoCode,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (promoCode.isValid) {
        AppliedPromoCodeNotice(
            code = promoCode.code,
            discountPercent = promoCode.discountPercent,
            onRemove = onRemove,
            modifier = modifier,
        )
    } else {
        NoticeWithAction(
            icon = AppIcons.Error,
            title = stringResource(Res.string.cart_promo_invalid, promoCode.code),
            subtitle = stringResource(Res.string.cart_promo_invalid_hint),
            style = NoticeStyle.Error,
            actionText = stringResource(Res.string.cart_promo_remove),
            onAction = onRemove,
            modifier = modifier,
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = AndroidUiModes.UI_MODE_NIGHT_YES)
@Composable
private fun CartPromoCodeNoticePreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(Spacing.ScreenPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CartPromoCodeNotice(
                    promoCode = CartUiState.AppliedPromoCode("SALE10", discountPercent = 10, isValid = true),
                    onRemove = {},
                )
                CartPromoCodeNotice(
                    promoCode = CartUiState.AppliedPromoCode("SALE10", discountPercent = 10, isValid = false),
                    onRemove = {},
                )
            }
        }
    }
}
