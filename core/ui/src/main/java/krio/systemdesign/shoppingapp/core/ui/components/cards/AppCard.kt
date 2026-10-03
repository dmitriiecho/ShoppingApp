package krio.systemdesign.shoppingapp.core.ui.components.cards

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.text.formatPrice
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.ui.theme.Spacing
import krio.systemdesign.shoppingapp.core.ui.theme.bodyLargeStrong

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val cardColors = CardDefaults.cardColors(containerColor = ShoppingAppTheme.colors.cardContainer)
    val elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, colors = cardColors, elevation = elevation, content = content)
    } else {
        Card(modifier = modifier, colors = cardColors, elevation = elevation, content = content)
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AppCardPreview() {
    ShoppingAppTheme {
        Surface {
            AppCard(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.CardPadding),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Order total",
                        style = MaterialTheme.typography.bodyLargeStrong,
                    )
                    OrderTotals(
                        subtotal = formatPrice(40_993L),
                        total = formatPrice(40_993L),
                    )
                }
            }
        }
    }
}
