package krio.systemdesign.shoppingapp.feature.cart.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.RemoveShoppingCart
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.feature.cart.ui.resources.Res
import krio.systemdesign.shoppingapp.feature.cart.ui.resources.cart_ui_clear_cart
import org.jetbrains.compose.resources.stringResource

// Red, like the confirmation it opens: clearing removes every item.
@Composable
fun ClearCartIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = AppIcons.RemoveShoppingCart,
            contentDescription = stringResource(Res.string.cart_ui_clear_cart),
            tint = MaterialTheme.colorScheme.error,
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = AndroidUiModes.UI_MODE_NIGHT_YES)
@Composable
private fun ClearCartIconButtonPreview() {
    ShoppingAppTheme {
        Surface {
            ClearCartIconButton(onClick = {}, modifier = Modifier.padding(8.dp))
        }
    }
}
