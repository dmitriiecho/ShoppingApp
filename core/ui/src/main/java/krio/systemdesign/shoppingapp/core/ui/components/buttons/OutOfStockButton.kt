package krio.systemdesign.shoppingapp.core.ui.components.buttons

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.R
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme

@Composable
fun OutOfStockButton(modifier: Modifier = Modifier) {
    WithoutTouchTargetReserve {
        FilledTonalButton(
            onClick = {},
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = ButtonDefaults.MinHeight),
            enabled = false,
        ) {
            Text(stringResource(R.string.core_ui_out_of_stock))
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OutOfStockButtonPreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutOfStockButton()
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = ShoppingAppTheme.colors.cardContainer,
                ) {
                    OutOfStockButton(modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}
