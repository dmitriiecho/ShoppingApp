package krio.systemdesign.shoppingapp.core.ui.components.inputs

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.components.buttons.CloseIconButton
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Search
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme

@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(AppIcons.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                CloseIconButton(onClick = onClear)
            }
        },
        singleLine = true,
    )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SearchFieldPreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("", "headphones").forEach { query ->
                    SearchField(
                        query = query,
                        onQueryChange = {},
                        onClear = {},
                        placeholder = "Search products",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
