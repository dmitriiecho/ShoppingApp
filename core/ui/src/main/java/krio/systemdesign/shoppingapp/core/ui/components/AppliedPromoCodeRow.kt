package krio.systemdesign.shoppingapp.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppliedPromoCodeRow(
    code: String,
    discountPercent: Int,
    modifier: Modifier = Modifier,
    onRemove: (() -> Unit)? = null,
) {
    // Высота как у TextButton, чтобы строка с кнопкой и без неё выглядела одинаково.
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Промокод $code · −$discountPercent%",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
        )
        if (onRemove != null) {
            // Сдвиг на внутренний отступ TextButton, чтобы текст встал по правому краю.
            TextButton(
                onClick = onRemove,
                modifier = Modifier.offset(x = 12.dp),
            ) {
                Text("Убрать")
            }
        }
    }
}
