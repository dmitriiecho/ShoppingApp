package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.screenstates.EmptyState
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ShoppingCart
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.feature.cart.impl.R
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.CartUiState

@Composable
internal fun CartEmptyState(
    promoCode: CartUiState.AppliedPromoCode?,
    onRemovePromo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // A Box, not a Column: the message stays in the middle of the screen and doesn't move
    // when the promo code notice appears or goes away.
    Box(modifier = modifier) {
        EmptyState(
            icon = AppIcons.ShoppingCart,
            title = stringResource(R.string.cart_empty_title),
            message = stringResource(R.string.cart_empty_message),
            modifier = Modifier.align(Alignment.Center),
        )
        // The promo code outlives the items: shown so it's clear it applies to the next purchase,
        // and so it can be removed.
        if (promoCode != null) {
            CartPromoCodeNotice(
                promoCode = promoCode,
                onRemove = onRemovePromo,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
            )
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CartEmptyStatePreview() {
    ShoppingAppTheme {
        Surface {
            CartEmptyState(promoCode = null, onRemovePromo = {}, modifier = Modifier.fillMaxSize())
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CartEmptyStateWithPromoCodePreview() {
    ShoppingAppTheme {
        Surface {
            CartEmptyState(
                promoCode = CartUiState.AppliedPromoCode("SALE10", discountPercent = 10, isValid = true),
                onRemovePromo = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
