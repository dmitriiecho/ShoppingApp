package krio.systemdesign.shoppingapp.feature.checkout.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShapeRadius
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.shared.ui.product.ProductImage
import krio.systemdesign.shoppingapp.shared.ui.text.formatPrice

@Composable
fun OrderItemRow(
    name: String,
    imageUrl: String,
    quantity: Int,
    unitPrice: String,
    total: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProductImage(
            imageUrl = imageUrl,
            modifier = Modifier.size(56.dp),
            cornerRadius = ShapeRadius.Small,
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = "$quantity × $unitPrice",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = total,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OrderItemRowPreview() {
    ShoppingAppTheme {
        Surface(color = ShoppingAppTheme.colors.cardContainer) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OrderItemRow(
                    name = "Wireless Headphones",
                    imageUrl = "",
                    quantity = 2,
                    unitPrice = formatPrice(14_999L),
                    total = formatPrice(14_999L * 2),
                )
                OrderItemRow(
                    name = "Mechanical Keyboard",
                    imageUrl = "",
                    quantity = 1,
                    unitPrice = formatPrice(10_995L),
                    total = formatPrice(10_995L),
                )
            }
        }
    }
}
