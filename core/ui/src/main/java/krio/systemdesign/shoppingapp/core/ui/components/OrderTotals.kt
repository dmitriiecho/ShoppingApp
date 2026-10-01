package krio.systemdesign.shoppingapp.core.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.R

// Все строки одним стилем текста, «Итого» выделено только жирностью. Если взять для «Итого» другой стиль
// (например, titleMedium), у него другой межбуквенный интервал, и строки выглядят набранными разными шрифтами.
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
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun PriceRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
) {
    val style = MaterialTheme.typography.bodyLarge.copy(fontWeight = fontWeight)
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
