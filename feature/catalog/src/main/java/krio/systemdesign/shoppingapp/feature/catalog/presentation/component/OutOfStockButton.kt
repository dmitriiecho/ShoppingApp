package krio.systemdesign.shoppingapp.feature.catalog.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.feature.catalog.R

// Показывается вместо кнопок корзины у закончившегося товара, даже если он уже лежит в корзине:
// количество менять нельзя, убрать товар можно на экране корзины, а при оформлении его отметит проверка.
@Composable
internal fun OutOfStockButton(modifier: Modifier = Modifier) {
    FilledTonalButton(
        onClick = {},
        enabled = false,
        modifier = modifier
            .fillMaxWidth()
            .height(ButtonDefaults.MinHeight),
    ) {
        Text(stringResource(R.string.catalog_out_of_stock))
    }
}
