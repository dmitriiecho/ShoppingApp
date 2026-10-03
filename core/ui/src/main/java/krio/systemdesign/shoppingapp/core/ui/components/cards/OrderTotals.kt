package krio.systemdesign.shoppingapp.core.ui.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.R
import krio.systemdesign.shoppingapp.core.ui.theme.totalsEmphasized

// Строки сумм: обычные — bodyLarge, «Итого» — totalsEmphasized (почему так — см. его описание).
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
            label = stringResource(R.string.core_ui_subtotal),
            value = subtotal,
        )
        if (discount != null) {
            PriceRow(
                label = stringResource(R.string.core_ui_discount),
                value = discount,
                valueColor = MaterialTheme.colorScheme.primary,
            )
        }
        PriceRow(
            label = stringResource(R.string.core_ui_total),
            value = total,
            style = MaterialTheme.typography.totalsEmphasized,
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
            style = style,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = style,
            color = valueColor,
        )
    }
}
