package krio.systemdesign.shoppingapp.core.ui.components.cards

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ConfirmationNumber
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ShoppingBag
import krio.systemdesign.shoppingapp.core.ui.text.formatPrice
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.ui.theme.Spacing

@Composable
fun SectionCard(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    AppCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(Spacing.CardPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
            content()
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SectionCardPreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SectionCard(
                    icon = AppIcons.ShoppingBag,
                    title = "Order items",
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
                SectionCard(
                    icon = AppIcons.ConfirmationNumber,
                    title = "Promo codes for testing",
                    subtitle = "Tap a code to put it in the field",
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PromoCodeCoupon(code = "SALE10", discountPercent = 10, onClick = {})
                        PromoCodeCoupon(code = "SALE25", discountPercent = 25, onClick = {})
                    }
                }
            }
        }
    }
}
