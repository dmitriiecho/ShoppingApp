package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productdetails.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.loading.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.feature.catalog.impl.R
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productdetails.ProductDetailsUiState
import krio.systemdesign.shoppingapp.feature.catalog.ui.ProductInfoPlaceholder
import krio.systemdesign.shoppingapp.shared.ui.text.formatPrice

// The name, price and description under the image; placeholders while the product loads.
@Composable
internal fun ProductInfoSection(
    // Never null for a loaded product, the only time the name is shown; the fallback is just for the type.
    name: String?,
    details: ProductDetailsUiState.Details,
    modifier: Modifier = Modifier,
) {
    val sectionModifier = modifier
        .fillMaxWidth()
        .padding(Spacing.ScreenPadding)
    when (details) {
        ProductDetailsUiState.Details.Loading -> ShimmerPlaceholder(modifier = sectionModifier) {
            ProductInfoPlaceholder()
        }
        is ProductDetailsUiState.Details.Loaded -> ProductInfo(
            name = name ?: stringResource(R.string.catalog_product_title),
            price = details.price,
            description = details.description,
            modifier = sectionModifier,
        )
        // Not shown: the screen shows these in place of everything.
        ProductDetailsUiState.Details.NotFound, ProductDetailsUiState.Details.Error -> Unit
    }
}

@Composable
private fun ProductInfo(
    name: String,
    price: Long,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = formatPrice(price),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        if (description.isNotBlank()) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductInfoSectionPreview() {
    ShoppingAppTheme {
        Surface {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    ProductDetailsUiState.Details.Loading,
                    ProductDetailsUiState.Details.Loaded(
                        price = 14999,
                        description = "Over-ear wireless headphones in matte black, with thick soft ear cushions.",
                        cartControl = ProductDetailsUiState.CartControl.InStock(quantity = 0, canIncrease = true),
                    ),
                ).forEach { details ->
                    ProductInfoSection(name = "Wireless Headphones", details = details)
                    HorizontalDivider()
                }
            }
        }
    }
}
