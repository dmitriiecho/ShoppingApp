package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productdetails.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.loading.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productdetails.ProductDetailsUiState
import krio.systemdesign.shoppingapp.feature.catalog.ui.OutOfStockButton
import krio.systemdesign.shoppingapp.shared.ui.cart.CartQuantityControl
import krio.systemdesign.shoppingapp.shared.ui.cart.CartQuantityControlPlaceholder

// The buttons at the bottom of the screen; a placeholder while the product loads.
@Composable
internal fun CartControlSection(
    details: ProductDetailsUiState.Details,
    onAddToCart: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onRemoveFromCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sectionModifier = modifier
        .fillMaxWidth()
        .padding(Spacing.ScreenPadding)
    when (details) {
        ProductDetailsUiState.Details.Loading -> ShimmerPlaceholder(modifier = sectionModifier) {
            CartQuantityControlPlaceholder()
        }
        is ProductDetailsUiState.Details.Loaded -> when (val control = details.cartControl) {
            is ProductDetailsUiState.CartControl.InStock -> CartQuantityControl(
                quantity = control.quantity,
                onAdd = onAddToCart,
                onIncrease = { onQuantityChange(control.quantity + 1) },
                onDecrease = { onQuantityChange(control.quantity - 1) },
                onRemoveAll = onRemoveFromCart,
                modifier = sectionModifier,
                canIncrease = control.canIncrease,
            )
            ProductDetailsUiState.CartControl.OutOfStock -> OutOfStockButton(modifier = sectionModifier)
        }
        // Not shown: the screen shows these in place of everything.
        ProductDetailsUiState.Details.NotFound, ProductDetailsUiState.Details.Error -> Unit
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CartControlSectionPreview() {
    ShoppingAppTheme {
        Surface {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    ProductDetailsUiState.Details.Loading,
                    loaded(ProductDetailsUiState.CartControl.InStock(quantity = 0, canIncrease = true)),
                    loaded(ProductDetailsUiState.CartControl.InStock(quantity = 2, canIncrease = true)),
                    // At the stock limit: "+" is off.
                    loaded(ProductDetailsUiState.CartControl.InStock(quantity = 1, canIncrease = false)),
                    loaded(ProductDetailsUiState.CartControl.OutOfStock),
                ).forEach { details ->
                    CartControlSection(
                        details = details,
                        onAddToCart = {},
                        onQuantityChange = {},
                        onRemoveFromCart = {},
                    )
                }
            }
        }
    }
}

private fun loaded(cartControl: ProductDetailsUiState.CartControl) = ProductDetailsUiState.Details.Loaded(
    price = 10995,
    description = "",
    cartControl = cartControl,
)
