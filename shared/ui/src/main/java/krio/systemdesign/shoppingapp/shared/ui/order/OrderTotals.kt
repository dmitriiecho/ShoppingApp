package krio.systemdesign.shoppingapp.shared.ui.order

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.core.designsystem.theme.bodyLargeStrong
import krio.systemdesign.shoppingapp.shared.ui.R
import krio.systemdesign.shoppingapp.shared.ui.text.formatPrice

// discount is the formatted amount; the minus sign is added here.
@Composable
fun OrderTotals(
    subtotal: String,
    total: String,
    modifier: Modifier = Modifier,
    discount: String? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        PriceRow(
            label = stringResource(R.string.shared_ui_subtotal),
            value = subtotal,
        )
        if (discount != null) {
            PriceRow(
                label = stringResource(R.string.shared_ui_discount),
                value = "−$discount",
                valueColor = MaterialTheme.colorScheme.primary,
            )
        }
        PriceRow(
            label = stringResource(R.string.shared_ui_total),
            value = total,
            style = MaterialTheme.typography.bodyLargeStrong,
        )
    }
}

@Composable
private fun PriceRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color.Unspecified,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = style,
        )
        Text(
            text = value,
            color = valueColor,
            style = style,
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OrderTotalsPreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = ShoppingAppTheme.colors.cardContainer,
                ) {
                    OrderTotals(
                        subtotal = formatPrice(40_993L),
                        total = formatPrice(36_894L),
                        modifier = Modifier.padding(Spacing.CardPadding),
                        discount = formatPrice(4_099L),
                    )
                }
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = ShoppingAppTheme.colors.cardContainer,
                ) {
                    OrderTotals(
                        subtotal = formatPrice(40_993L),
                        total = formatPrice(40_993L),
                        modifier = Modifier.padding(Spacing.CardPadding),
                    )
                }
            }
        }
    }
}
