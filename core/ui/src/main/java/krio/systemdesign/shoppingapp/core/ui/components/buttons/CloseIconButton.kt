package krio.systemdesign.shoppingapp.core.ui.components.buttons

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.R
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme

@Composable
fun CloseIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = stringResource(R.string.core_ui_close),
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CloseIconButtonPreview() {
    ShoppingAppTheme {
        // Same tint as in the app: in TopAppBar actions and in SearchField.
        Surface(contentColor = MaterialTheme.colorScheme.onSurfaceVariant) {
            CloseIconButton(onClick = {}, modifier = Modifier.padding(8.dp))
        }
    }
}
