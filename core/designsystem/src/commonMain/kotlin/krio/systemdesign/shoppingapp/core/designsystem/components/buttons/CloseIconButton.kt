package krio.systemdesign.shoppingapp.core.designsystem.components.buttons

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
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Close
import krio.systemdesign.shoppingapp.core.designsystem.resources.Res
import krio.systemdesign.shoppingapp.core.designsystem.resources.designsystem_close
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun CloseIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = AppIcons.Close,
            contentDescription = stringResource(Res.string.designsystem_close),
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = AndroidUiModes.UI_MODE_NIGHT_YES)
@Composable
private fun CloseIconButtonPreview() {
    ShoppingAppTheme {
        // Same tint as in the app, in TopAppBar actions.
        Surface(contentColor = MaterialTheme.colorScheme.onSurfaceVariant) {
            CloseIconButton(onClick = {}, modifier = Modifier.padding(8.dp))
        }
    }
}
