package krio.systemdesign.shoppingapp.core.ui.components.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.core.ui.R

// Показывается вместо кнопок корзины (CartQuantityControl) у закончившегося товара, даже если он уже лежит в корзине:
// количество менять нельзя, убрать товар можно на экране корзины, а при оформлении его отметит проверка.
// Высотой как CartQuantityControl, которую заменяет; с крупным шрифтом растёт вместе с текстом.
@Composable
fun OutOfStockButton(modifier: Modifier = Modifier) {
    WithoutTouchTargetReserve {
        FilledTonalButton(
            onClick = {},
            enabled = false,
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = ButtonDefaults.MinHeight),
        ) {
            Text(stringResource(R.string.core_ui_out_of_stock))
        }
    }
}
